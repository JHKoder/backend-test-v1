package im.bigs.pg.api.payment.`in`

import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class CreatePaymentRequest(
    @field:Min(1, message = "파트너 PG ID는 1 이상 이여야 합니다.")
    val partnerId: Long,
    @field:Min(1, message = "금액은 1 이상 이여야 합니다.")
    val amount: BigDecimal,
    val cardBin: String? = null,
    val cardLast4: String? = null,
    val productName: String? = null,
)
