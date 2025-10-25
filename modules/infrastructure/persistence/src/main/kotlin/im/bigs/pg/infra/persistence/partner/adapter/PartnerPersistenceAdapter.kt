package im.bigs.pg.infra.persistence.partner.adapter

import im.bigs.pg.application.partner.port.out.PartnerOutPort
import im.bigs.pg.domain.partner.Partner
import im.bigs.pg.infra.persistence.partner.repository.PartnerJpaRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PartnerPersistenceAdapter(
    private val repo: PartnerJpaRepository,
) : PartnerOutPort {
    override fun findById(id: Long): Optional<Partner> {
        return repo.findById(id).map { Partner(id = it.id!!, code = it.code, name = it.name, active = it.active) }
    }
}
