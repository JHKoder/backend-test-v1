package im.bigs.pg.infra.persistence.partner.adapter

import im.bigs.pg.application.partner.port.out.FeePolicyOutPort
import im.bigs.pg.domain.partner.FeePolicy
import im.bigs.pg.infra.persistence.partner.repository.FeePolicyJpaRepository
import org.springframework.stereotype.Component
import java.time.Instant

/** 수수료 정책 조회 어댑터. */
@Component
class FeePolicyPersistenceAdapter(
    private val repo: FeePolicyJpaRepository,
) : FeePolicyOutPort {
    override fun findEffectivePolicy(partnerId: Long, at: Instant): FeePolicy? =
        repo.findTop1ByPartnerIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(partnerId, at)?.let {
            FeePolicy(
                id = it.id,
                partnerId = it.partnerId,
                effectiveFrom = it.effectiveFrom,
                percentage = it.percentage,
                fixedFee = it.fixedFee,
            )
        }
}
