package im.bigs.pg.external.pg.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import im.bigs.pg.external.pg.client.`in`.PgApiKeyRequest
import im.bigs.pg.external.util.SecurePaymentEncoder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class SecurePaymentEncoderTest {

    private val mapper = jacksonObjectMapper()

    @Test
    @DisplayName("암호화 성공")
    fun `암호화 성공`() {
        // Given
        val apiKey = "11111111-1111-4111-8111-111111111111"
        val iv = "AAAAAAAAAAAAAAAA"
        val request = PgApiKeyRequest("1111-1111-1111-1111", "19900101", "1227", "12", BigDecimal.valueOf(1000))

        // When
        val encrypted = SecurePaymentEncoder.encryptToEnc(apiKey, iv, request)

        // Then
        val decrypted = SecurePaymentEncoder.decrypt(apiKey, iv, encrypted)
        val decryptedRequest = mapper.readValue<PgApiKeyRequest>(decrypted)

        assertEquals(request, decryptedRequest)
    }
}
