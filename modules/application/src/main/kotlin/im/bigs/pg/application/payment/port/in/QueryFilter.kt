package im.bigs.pg.application.payment.port.`in`

import java.time.Instant

data class QueryFilter(
    val partnerId: Long? = null,
    val status: String? = null,
    val from: Instant? = null,
    val to: Instant? = null,
    val cursor: String? = null,
    val limit: Int = 20,
)
