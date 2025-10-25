package im.bigs.pg.application.payment.service

import im.bigs.pg.application.partner.port.out.FeePolicyOutPort
import im.bigs.pg.application.partner.port.out.PartnerOutPort
import im.bigs.pg.application.payment.port.`in`.PaymentCommand
import im.bigs.pg.application.payment.port.`in`.PaymentUseCase
import im.bigs.pg.application.payment.port.out.PaymentOutPort
import im.bigs.pg.application.pg.port.out.PgApproveRequest
import im.bigs.pg.application.pg.port.out.PgClientOutPort
import im.bigs.pg.common.exception.ApiException
import im.bigs.pg.common.exception.ErrorCode
import im.bigs.pg.domain.calculation.FeeCalculator
import im.bigs.pg.domain.payment.Payment
import im.bigs.pg.domain.payment.PaymentStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.ZoneOffset

/**
 * 결제 생성 유스케이스 구현체.
 * - 입력(REST 등) → 도메인/외부PG/영속성 포트를 순차적으로 호출하는 흐름을 담당합니다.
 * - 수수료 정책 조회 및 적용(계산)은 도메인 유틸리티를 통해 수행합니다.
 */
@Service
class PaymentService(
    private val partnerRepository: PartnerOutPort,
    private val feePolicyRepository: FeePolicyOutPort,
    private val paymentRepository: PaymentOutPort,
    private val pgClients: List<PgClientOutPort>,
) : PaymentUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun pay(command: PaymentCommand): Payment {
        val partner = partnerRepository.findById(command.partnerId)
            .orElseThrow { ApiException(ErrorCode.PARTNER_NOT_FOUND) }

        require(partner.active) { "파트너가 비활성 상태입니다. id: ${partner.id}" }

        val pgClient = pgClients.firstOrNull { it.supports(partner.code) }
            ?: throw ApiException(ErrorCode.PG_CLIENT_NOT_FOUND)

        val approve = pgClient.approve(
            PgApproveRequest(
                partnerId = partner.id,
                amount = command.amount,
                cardBin = command.cardBin,
                cardLast4 = command.cardLast4,
                productName = command.productName,
            ),
        )

        val feePolicy = feePolicyRepository.findEffectivePolicy(partner.id)
            ?: throw ApiException(ErrorCode.PARTNER_FEE_POLICY)

        val (fee, net) = FeeCalculator.calculateFee(
            command.amount,
            feePolicy.percentage,
            feePolicy.fixedFee
        )

        val payment = Payment(
            partnerId = partner.id,
            amount = command.amount,
            appliedFeeRate = feePolicy.percentage,
            feeAmount = fee,
            netAmount = net,
            cardBin = cardBinMask(command.cardBin),
            cardLast4 = command.cardLast4,
            approvalCode = approve.approvalCode,
            approvedAt = approve.approvedAt.toInstant(ZoneOffset.UTC),
            status = PaymentStatus.APPROVED,
        )

        log.info("Payment created: ${payment.partnerId} , approvedAt=${payment.approvedAt} ")
        return paymentRepository.save(payment)
    }

    private fun cardBinMask(cardBin: String?): String {
        if (cardBin.isNullOrBlank()) return "**"
        if (cardBin.length <= 2) return "**"

        val visiblePart = cardBin.take(2)
        val maskedPart = "*".repeat(cardBin.length - 2)
        return visiblePart + maskedPart
    }
}
