package com.skysam.datossegurosFirebaseFinal.database.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.DatosSeguros
import com.skysam.datossegurosFirebaseFinal.common.SecureLog
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
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

    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val AES_KEY_ALIAS = "datos_seguros_pin_key"
    private const val AES_KEY_SIZE_BITS = 256
    private const val GCM_TAG_LENGTH_BITS = 128
    private const val GCM_IV_LENGTH_BYTES = 12
    private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"

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

        val hashEnc = encryptString(hashB64)
        val saltEnc = encryptString(saltB64)

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
                val hashB64 = decryptString(encHash)
                val saltB64 = decryptString(encSalt)
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
            val hashEnc = encryptString(legacyHashB64) ?: return
            val saltEnc = encryptString(legacySaltB64) ?: return
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

    private fun getOrCreateAesKey(): SecretKey? {
        return try {
            val ks = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            (ks.getKey(AES_KEY_ALIAS, null) as? SecretKey) ?: createAesKey()
        } catch (t: Throwable) {
            SecureLog.w(TAG, "Failed to access AndroidKeyStore", t)
            null
        }
    }

    private fun createAesKey(): SecretKey? {
        return try {
            val generator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            val spec = KeyGenParameterSpec.Builder(
                AES_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(AES_KEY_SIZE_BITS)
                .setRandomizedEncryptionRequired(true)
                .build()
            generator.init(spec)
            generator.generateKey()
        } catch (t: Throwable) {
            SecureLog.w(TAG, "Failed to create AES key", t)
            null
        }
    }

    private fun encryptString(plaintext: String): String? {
        val key = getOrCreateAesKey() ?: return null
        return try {
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv
            val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            val payload = ByteArray(iv.size + ciphertext.size)
            System.arraycopy(iv, 0, payload, 0, iv.size)
            System.arraycopy(ciphertext, 0, payload, iv.size, ciphertext.size)
            Base64.encodeToString(payload, Base64.NO_WRAP)
        } catch (t: Throwable) {
            SecureLog.w(TAG, "Encrypt failed", t)
            null
        }
    }

    private fun decryptString(payloadB64: String): String? {
        val key = getOrCreateAesKey() ?: return null
        return try {
            val payload = Base64.decode(payloadB64, Base64.NO_WRAP)
            if (payload.size <= GCM_IV_LENGTH_BYTES) return null
            val iv = payload.copyOfRange(0, GCM_IV_LENGTH_BYTES)
            val ct = payload.copyOfRange(GCM_IV_LENGTH_BYTES, payload.size)
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
            String(cipher.doFinal(ct), Charsets.UTF_8)
        } catch (t: Throwable) {
            SecureLog.w(TAG, "Decrypt failed", t)
            null
        }
    }
}
