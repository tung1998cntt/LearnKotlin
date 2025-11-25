package com.example.learnkotlin.core.secure

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object KeyStoreHelper {

    private const val AES_KEY_ALIAS = "APP_AES_KEY"
    private const val HMAC_KEY_ALIAS = "APP_HMAC_KEY"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    fun getAesKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        // key đã có → trả về
        keyStore.getKey(AES_KEY_ALIAS, null)?.let { return it as SecretKey }

        // tạo AES-256 key
        val keyGenerator = KeyGenerator.getInstance("AES", ANDROID_KEYSTORE)

        val spec = KeyGenParameterSpec.Builder(
            AES_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)

        return keyGenerator.generateKey()
    }

    fun getHmacKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        keyStore.getKey(HMAC_KEY_ALIAS, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance("HmacSHA256", ANDROID_KEYSTORE)

        val spec = KeyGenParameterSpec.Builder(
            HMAC_KEY_ALIAS,
            KeyProperties.PURPOSE_SIGN
        )
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)

        return keyGenerator.generateKey()
    }
}