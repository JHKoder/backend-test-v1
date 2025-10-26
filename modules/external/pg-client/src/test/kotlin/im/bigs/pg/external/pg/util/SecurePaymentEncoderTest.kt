package im.bigs.pg.external.pg.util

import im.bigs.pg.external.pg.client.`in`.PgApiKeyRequest
import im.bigs.pg.external.util.SecurePaymentEncoder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class SecurePaymentEncoderTest {

    @Test
    @DisplayName("암호화 성공")
    fun `암호화 성공`() {
        val request = PgApiKeyRequest("1111-1111-1111-1111", "19900101", "1227", "12", BigDecimal.valueOf(1000))

        val result =
            SecurePaymentEncoder.encryptToEnc("11111111-1111-4111-8111-111111111111", "AAAAAAAAAAAAAAAA", request)

        assertEquals(
            result,
            "FlrQ_ZFCA9WC7HIkPzKFpnzv1AX0n7zodWtWRo6X6-ccrzwEkwOGAJyfC4cwihNy4EtwXS6yx2FBHcOP44mxDAZvv38YF6LLnBSBW2zpsvBUgImnuR6Gc_z1CTID_tuA-Rpmrhjoguyl3PxnF9A5dhTLM6T0HO4JxbA"
        )
    }
}
