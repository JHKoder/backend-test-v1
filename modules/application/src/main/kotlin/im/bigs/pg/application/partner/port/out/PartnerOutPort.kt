package im.bigs.pg.application.partner.port.out

import im.bigs.pg.domain.partner.Partner
import java.util.*

interface PartnerOutPort {
    fun findById(id: Long): Optional<Partner>
}
