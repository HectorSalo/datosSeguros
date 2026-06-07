package com.skysam.datossegurosFirebaseFinal.database.sharedPreference

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.skysam.datossegurosFirebaseFinal.common.SecureLog
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal object KeystoreCipher {
    private const val TAG = "KeystoreCipher"

    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val AES_KEY_ALIAS = "datos_seguros_pin_key"
    private const val AES_KEY_SIZE_BITS = 256
    private const val GCM_TAG_LENGTH_BITS = 128
    private const val GCM_IV_LENGTH_BYTES = 12
    private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"

    fun encrypt(plaintext: String): String? {
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

    fun decrypt(payloadB64: String): String? {
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
}
