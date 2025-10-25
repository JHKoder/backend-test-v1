package im.bigs.pg.api.payment.out

import com.fasterxml.jackson.annotation.JsonFormat
import im.bigs.pg.domain.payment.Payment
import im.bigs.pg.domain.payment.PaymentStatus
import java.math.BigDecimal
import java.time.Instant

data class PaymentResponse(
    val id: Long?,
    val partnerId: Long,
    val amount: BigDecimal,
    val appliedFeeRate: BigDecimal,
    val feeAmount: BigDecimal,
    val netAmount: BigDecimal,
    val cardLast4: String?,
    val approvalCode: String,
    @get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val approvedAt: Instant,
    val status: PaymentStatus,
    @get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val createdAt: Instant,
) {
    companion object {
        fun from(p: Payment) = PaymentResponse(
            id = p.id,
            partnerId = p.partnerId,
            amount = p.amount,
            appliedFeeRate = p.appliedFeeRate.stripTrailingZeros(),
            feeAmount = p.feeAmount,
            netAmount = p.netAmount,
            cardLast4 = p.cardLast4,
            approvalCode = p.approvalCode,
            approvedAt = p.approvedAt,
            status = p.status,
            createdAt = p.createdAt,
        )
    }
}

