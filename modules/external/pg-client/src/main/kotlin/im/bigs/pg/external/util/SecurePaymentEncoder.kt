package im.bigs.pg.external.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object SecurePaymentEncoder {
    private val mapper = jacksonObjectMapper()

    fun encryptToEnc(apiKey: String, iv: String, payload: Any): String {
        val json = mapper.writeValueAsString(payload)
        val keyBytes = sha256(apiKey)
        val ivBytes = Base64.getUrlDecoder().decode(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(keyBytes, "AES"),
            GCMParameterSpec(128, ivBytes)
        )

        val cipherBytes = cipher.doFinal(json.toByteArray(Charsets.UTF_8))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(cipherBytes)
    }

    fun decrypt(apiKey: String, iv: String, enc: String): String {
        val keyBytes = MessageDigest.getInstance("SHA-256")
            .digest(apiKey.toByteArray(Charsets.UTF_8))
        val ivBytes = Base64.getUrlDecoder().decode(iv)
        val cipherBytes = Base64.getUrlDecoder().decode(enc)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(keyBytes, "AES"),
            GCMParameterSpec(128, ivBytes)
        )

        val plainBytes = cipher.doFinal(cipherBytes)
        return String(plainBytes, Charsets.UTF_8)
    }

    private fun sha256(s: String): ByteArray =
        MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
}
