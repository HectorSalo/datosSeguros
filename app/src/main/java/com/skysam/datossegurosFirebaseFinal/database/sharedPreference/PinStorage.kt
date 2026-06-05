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
    private const val DEFAULT_PIN = "0000"
    private const val KEY_PIN_HASH = "pinHash"
    private const val KEY_PIN_SALT = "pinSalt"
    private const val SALT_LENGTH_BYTES = 16
    private const val PBKDF2_ITERATIONS = 100_000
    private const val PBKDF2_KEY_LENGTH_BITS = 256
    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"

    private fun prefs(): SharedPreferences =
        DatosSeguros.DatosSeguros.getContext().getSharedPreferences(
            Auth.getCurrenUser()!!.uid,
            Context.MODE_PRIVATE
        )

    @JvmStatic
    fun verify(pin: String): Boolean {
        val sp = prefs()
        val storedHash = sp.getString(KEY_PIN_HASH, null)
        val storedSalt = sp.getString(KEY_PIN_SALT, null)

        if (storedHash != null && storedSalt != null) {
            try {
                val salt = Base64.decode(storedSalt, Base64.NO_WRAP)
                val expected = Base64.decode(storedHash, Base64.NO_WRAP)
                val computed = pbkdf2(pin, salt)
                return MessageDigest.isEqual(computed, expected)
            } catch (t: Throwable) {
                SecureLog.w(TAG, "Hash verify failed; falling back to legacy", t)
            }
        }

        return verifyLegacyAndMigrateIfNeeded(sp, pin)
    }

    @JvmStatic
    fun save(pin: String) {
        val salt = ByteArray(SALT_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(pin, salt)
        prefs().edit()
            .putString(KEY_PIN_HASH, Base64.encodeToString(hash, Base64.NO_WRAP))
            .putString(KEY_PIN_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString(Constants.PREFERENCE_PIN_RESPALDO, pin)
            .apply()
    }

    @JvmStatic
    fun reset() {
        prefs().edit()
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .putString(Constants.PREFERENCE_PIN_RESPALDO, DEFAULT_PIN)
            .apply()
    }

    private fun verifyLegacyAndMigrateIfNeeded(sp: SharedPreferences, pin: String): Boolean {
        val legacy = sp.getString(Constants.PREFERENCE_PIN_RESPALDO, DEFAULT_PIN)
        if (pin != legacy) return false
        try {
            save(pin)
        } catch (t: Throwable) {
            SecureLog.w(TAG, "PIN auto-migration to hash failed; staying on legacy", t)
        }
        return true
    }

    private fun pbkdf2(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance(PBKDF2_ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
