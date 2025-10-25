package im.bigs.pg.external.pg.client

import im.bigs.pg.application.pg.port.out.PgApproveRequest
import im.bigs.pg.application.pg.port.out.PgApproveResult
import im.bigs.pg.application.pg.port.out.PgClientOutPort
import im.bigs.pg.domain.payment.PaymentStatus
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Component
class PgMockClient : PgClientOutPort {
    override fun supports(pgType: String): Boolean {
        return pgType.uppercase() == PgClientType.MOCK.name
    }

    override fun approve(request: PgApproveRequest): PgApproveResult {
        val dateOfMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd"))
        val randomDigits = Random.nextInt(9999).toString().padStart(4, '0')
        return PgApproveResult(
            approvalCode = "$dateOfMonth$randomDigits",
            approvedAt = LocalDateTime.now(ZoneOffset.UTC),
            PaymentStatus.APPROVED,
            request.cardLast4,
            request.amount
        )
    }
}
