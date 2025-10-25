package im.bigs.pg.infra.persistence.payment.adapter

import im.bigs.pg.application.payment.port.out.*
import im.bigs.pg.domain.payment.Payment
import im.bigs.pg.domain.payment.PaymentStatus
import im.bigs.pg.infra.persistence.payment.entity.PaymentEntity
import im.bigs.pg.infra.persistence.payment.repository.PaymentJpaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/** PaymentOutPort 구현체(JPA 기반). */
@Component
class PaymentPersistenceAdapter(
    private val repo: PaymentJpaRepository,
) : PaymentOutPort {

    @Transactional
    override fun save(payment: Payment): Payment =
        repo.save(payment.toEntity()).toDomain()

    @Transactional(readOnly = true)
    override fun findBy(query: PaymentQuery): PaymentPage {
        val pageSize = query.limit
        val list = repo.pageBy(
            partnerId = query.partnerId,
            status = query.status?.name,
            fromAt = query.from,
            toAt = query.to,
            cursorCreatedAt = query.cursorCreatedAt,
            cursorId = query.cursorId,
            org = PageRequest.of(0, pageSize + 1),
        )
        val hasNext = list.size > pageSize
        val items = list.take(pageSize)
        val last = items.lastOrNull()

        return PaymentPage(
            items = items.map { it.toDomain() },
            hasNext = hasNext,
            nextCursorCreatedAt = last?.createdAt,
            nextCursorId = last?.id,
        )
    }

    override fun summary(filter: PaymentSummaryFilter): PaymentSummaryProjection {
        val list = repo.summary(
            partnerId = filter.partnerId,
            status = filter.status?.name,
            fromAt = filter.from,
            toAt = filter.to
        )
        val arr = list.first()
        val cnt = (arr[0] as Number).toLong()
        val totalAmount = arr[1] as java.math.BigDecimal
        val totalNet = arr[2] as java.math.BigDecimal
        return PaymentSummaryProjection(cnt, totalAmount, totalNet)
    }

    /** 도메인 → 엔티티 매핑. */
    private fun Payment.toEntity() =
        PaymentEntity(
            id = this.id,
            partnerId = this.partnerId,
            amount = this.amount,
            appliedFeeRate = this.appliedFeeRate,
            feeAmount = this.feeAmount,
            netAmount = this.netAmount,
            cardBin = this.cardBin,
            cardLast4 = this.cardLast4,
            approvalCode = this.approvalCode,
            approvedAt = this.approvedAt,
            status = this.status.name,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )

    /** 엔티티 → 도메인 매핑. */
    private fun PaymentEntity.toDomain() =
        Payment(
            id = this.id,
            partnerId = this.partnerId,
            amount = this.amount,
            appliedFeeRate = this.appliedFeeRate,
            feeAmount = this.feeAmount,
            netAmount = this.netAmount,
            cardBin = this.cardBin,
            cardLast4 = this.cardLast4,
            approvalCode = this.approvalCode,
            approvedAt = this.approvedAt,
            status = PaymentStatus.valueOf(this.status),
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
}
