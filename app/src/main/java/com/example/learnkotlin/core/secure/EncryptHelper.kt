package com.example.learnkotlin.core.secure

import javax.crypto.Cipher
import android.util.Base64
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object EncryptHelper {

    private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val TAG_LENGTH = 128  // GCM tag 16 bytes

    // Encrypt + HMAC
    fun encrypt(plain: String): String {
        val aesKey = KeyStoreHelper.getAesKey()
        val hmacKey = KeyStoreHelper.getHmacKey()

        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)

        val iv = cipher.iv // 12 bytes
        val encrypted = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))

        // HMAC(iv + encrypted)
        val dataToSign = iv + encrypted
        val signature = createHmac(hmacKey, dataToSign)

        // Base64(iv | encrypted | hmac)
        val finalBytes = iv + encrypted + signature
        return Base64.encodeToString(finalBytes, Base64.NO_WRAP)
    }

    // Decrypt
    fun decrypt(encoded: String): String {
        val aesKey = KeyStoreHelper.getAesKey()
        val hmacKey = KeyStoreHelper.getHmacKey()

        val raw = Base64.decode(encoded, Base64.NO_WRAP)

        val iv = raw.sliceArray(0 until 12)
        val encryptedStart = 12
        val hmacStart = raw.size - 32

        val encrypted = raw.sliceArray(encryptedStart until hmacStart)
        val signature = raw.sliceArray(hmacStart until raw.size)

        // verify HMAC
        val dataToSign = iv + encrypted
        val validSignature = createHmac(hmacKey, dataToSign)

        if (!validSignature.contentEquals(signature)) {
            throw SecurityException("HMAC mismatch — data may be tampered!")
        }

        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, aesKey, GCMParameterSpec(TAG_LENGTH, iv))

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }

    private fun createHmac(key: SecretKey, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(key)
        return mac.doFinal(data)
    }
}