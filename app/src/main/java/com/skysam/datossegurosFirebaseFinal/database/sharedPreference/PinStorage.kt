package com.skysam.datossegurosFirebaseFinal.database.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.DatosSeguros
import com.skysam.datossegurosFirebaseFinal.common.SecureLog
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PinStorage {
    private const val TAG = "PinStorage"
    private const val KEY_PIN_HASH = "pinHash"
    private const val KEY_PIN_SALT = "pinSalt"
    private const val KEY_PIN_HASH_ENC = "pinHashEnc"
    private const val KEY_PIN_SALT_ENC = "pinSaltEnc"
    private const val KEY_PIN_FAILED_COUNT = "pinFailedCount"
    private const val KEY_PIN_LOCKOUT_UNTIL = "pinLockoutUntilEpochMs"
    private const val SALT_LENGTH_BYTES = 16
    private const val PBKDF2_ITERATIONS = 100_000
    private const val PBKDF2_KEY_LENGTH_BITS = 256
    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"

    private const val LOCKOUT_THRESHOLD_SOFT = 5
    private const val LOCKOUT_THRESHOLD_HARD = 10
    private const val LOCKOUT_DURATION_SOFT_MS = 60_000L
    private const val LOCKOUT_DURATION_HARD_MS = 5 * 60_000L

    private fun prefs(): SharedPreferences =
        DatosSeguros.DatosSeguros.getContext().getSharedPreferences(
            Auth.getCurrenUser()!!.uid,
            Context.MODE_PRIVATE
        )

    @JvmStatic
    fun verify(pin: String): Boolean {
        val sp = prefs()

        if (System.currentTimeMillis() < currentLockoutUntil(sp)) {
            return false
        }

        val ok = runVerifyAgainstStores(sp, pin)
        if (ok) {
            clearAttempts(sp)
            clearPlaintextResidueIfMigrated(sp)
        } else {
            recordFailedAttempt(sp)
        }
        return ok
    }

    @JvmStatic
    fun save(pin: String) {
        val salt = ByteArray(SALT_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(pin, salt)
        val hashB64 = Base64.encodeToString(hash, Base64.NO_WRAP)
        val saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP)

        val hashEnc = KeystoreCipher.encrypt(hashB64)
        val saltEnc = KeystoreCipher.encrypt(saltB64)

        val editor = prefs().edit()
        if (hashEnc != null && saltEnc != null) {
            editor.putString(KEY_PIN_HASH_ENC, hashEnc)
                .putString(KEY_PIN_SALT_ENC, saltEnc)
                .remove(KEY_PIN_HASH)
                .remove(KEY_PIN_SALT)
        } else {
            SecureLog.w(TAG, "Encryption unavailable; writing hash/salt unencrypted")
            editor.putString(KEY_PIN_HASH, hashB64)
                .putString(KEY_PIN_SALT, saltB64)
                .remove(KEY_PIN_HASH_ENC)
                .remove(KEY_PIN_SALT_ENC)
        }
        editor.remove(Constants.PREFERENCE_PIN_RESPALDO)
            .apply()
    }

    @JvmStatic
    fun reset() {
        prefs().edit()
            .remove(KEY_PIN_HASH_ENC)
            .remove(KEY_PIN_SALT_ENC)
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .remove(KEY_PIN_FAILED_COUNT)
            .remove(KEY_PIN_LOCKOUT_UNTIL)
            .remove(Constants.PREFERENCE_PIN_RESPALDO)
            .apply()
    }

    @JvmStatic
    fun isLocked(): Boolean = lockoutRemainingMs() > 0L

    @JvmStatic
    fun lockoutRemainingMs(): Long {
        val remaining = currentLockoutUntil(prefs()) - System.currentTimeMillis()
        return if (remaining > 0L) remaining else 0L
    }

    private fun runVerifyAgainstStores(sp: SharedPreferences, pin: String): Boolean {
        val encHash = sp.getString(KEY_PIN_HASH_ENC, null)
        val encSalt = sp.getString(KEY_PIN_SALT_ENC, null)
        if (encHash != null && encSalt != null) {
            try {
                val hashB64 = KeystoreCipher.decrypt(encHash)
                val saltB64 = KeystoreCipher.decrypt(encSalt)
                if (hashB64 != null && saltB64 != null) {
                    return verifyHashEncoded(pin, hashB64, saltB64)
                }
            } catch (t: Throwable) {
                SecureLog.w(TAG, "Encrypted verify failed; trying legacy hash", t)
            }
        }

        val legacyHash = sp.getString(KEY_PIN_HASH, null)
        val legacySalt = sp.getString(KEY_PIN_SALT, null)
        if (legacyHash != null && legacySalt != null) {
            try {
                if (verifyHashEncoded(pin, legacyHash, legacySalt)) {
                    promoteLegacyHashToEncrypted(sp, legacyHash, legacySalt)
                    return true
                }
                return false
            } catch (t: Throwable) {
                SecureLog.w(TAG, "Legacy hash verify failed; falling back to plaintext", t)
            }
        }

        return verifyLegacyAndMigrateIfNeeded(sp, pin)
    }

    private fun verifyHashEncoded(pin: String, hashB64: String, saltB64: String): Boolean {
        val salt = Base64.decode(saltB64, Base64.NO_WRAP)
        val expected = Base64.decode(hashB64, Base64.NO_WRAP)
        val computed = pbkdf2(pin, salt)
        return MessageDigest.isEqual(computed, expected)
    }

    private fun verifyLegacyAndMigrateIfNeeded(sp: SharedPreferences, pin: String): Boolean {
        val legacy = sp.getString(Constants.PREFERENCE_PIN_RESPALDO, null) ?: return false
        if (pin != legacy) return false
        try {
            save(pin)
        } catch (t: Throwable) {
            SecureLog.w(TAG, "PIN auto-migration failed; staying on legacy", t)
        }
        return true
    }

    private fun clearPlaintextResidueIfMigrated(sp: SharedPreferences) {
        val migrated = sp.getString(KEY_PIN_HASH_ENC, null) != null ||
                sp.getString(KEY_PIN_HASH, null) != null
        if (migrated && sp.contains(Constants.PREFERENCE_PIN_RESPALDO)) {
            sp.edit().remove(Constants.PREFERENCE_PIN_RESPALDO).apply()
        }
    }

    private fun promoteLegacyHashToEncrypted(
        sp: SharedPreferences,
        legacyHashB64: String,
        legacySaltB64: String
    ) {
        try {
            val hashEnc = KeystoreCipher.encrypt(legacyHashB64) ?: return
            val saltEnc = KeystoreCipher.encrypt(legacySaltB64) ?: return
            sp.edit()
                .putString(KEY_PIN_HASH_ENC, hashEnc)
                .putString(KEY_PIN_SALT_ENC, saltEnc)
                .remove(KEY_PIN_HASH)
                .remove(KEY_PIN_SALT)
                .apply()
        } catch (t: Throwable) {
            SecureLog.w(TAG, "Legacy-to-encrypted promotion failed", t)
        }
    }

    private fun pbkdf2(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance(PBKDF2_ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun currentFailedCount(sp: SharedPreferences): Int =
        sp.getInt(KEY_PIN_FAILED_COUNT, 0)

    private fun currentLockoutUntil(sp: SharedPreferences): Long =
        sp.getLong(KEY_PIN_LOCKOUT_UNTIL, 0L)

    private fun recordFailedAttempt(sp: SharedPreferences) {
        val next = currentFailedCount(sp) + 1
        val duration = computeLockoutDurationMs(next)
        val editor = sp.edit().putInt(KEY_PIN_FAILED_COUNT, next)
        if (duration > 0L) {
            editor.putLong(KEY_PIN_LOCKOUT_UNTIL, System.currentTimeMillis() + duration)
        }
        editor.apply()
    }

    private fun clearAttempts(sp: SharedPreferences) {
        sp.edit()
            .remove(KEY_PIN_FAILED_COUNT)
            .remove(KEY_PIN_LOCKOUT_UNTIL)
            .apply()
    }

    private fun computeLockoutDurationMs(failedCount: Int): Long = when {
        failedCount < LOCKOUT_THRESHOLD_SOFT -> 0L
        failedCount < LOCKOUT_THRESHOLD_HARD -> LOCKOUT_DURATION_SOFT_MS
        else -> LOCKOUT_DURATION_HARD_MS
    }
}
