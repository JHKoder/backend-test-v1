package im.bigs.pg.application.payment.port.out

import com.fasterxml.jackson.annotation.JsonFormat
import im.bigs.pg.domain.payment.Payment
import java.time.Instant

/** 페이지 결과. */
data class PaymentPage(
    val items: List<Payment>,
    val hasNext: Boolean,
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val nextCursorCreatedAt: Instant?,
    val nextCursorId: Long?,
)
