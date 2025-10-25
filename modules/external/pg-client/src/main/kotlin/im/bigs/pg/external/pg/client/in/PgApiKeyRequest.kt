package im.bigs.pg.external.pg.client.`in`

import im.bigs.pg.application.pg.port.out.PgApproveRequest
import java.math.BigDecimal

data class PgApiKeyRequest(
    val cardNumber: String,
    val birthDate: String,
    val expiry: String,
    val password: String,
    val amount: BigDecimal,
) {

    companion object {

        fun fakeCardOf(pgApprove: PgApproveRequest): PgApiKeyRequest {
            return PgApiKeyRequest(
                cardNumber = "1111-1111-1111-1111",
                birthDate = "19900101",
                expiry = "1227",
                password = "12",
                amount = pgApprove.amount)
        }
    }
}