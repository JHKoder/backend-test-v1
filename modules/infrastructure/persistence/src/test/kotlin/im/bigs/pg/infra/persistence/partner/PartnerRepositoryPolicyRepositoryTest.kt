package im.bigs.pg.infra.persistence.partner

import im.bigs.pg.infra.persistence.config.JpaConfig
import im.bigs.pg.infra.persistence.partner.entity.FeePolicyEntity
import im.bigs.pg.infra.persistence.partner.repository.FeePolicyJpaRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import java.math.BigDecimal
import java.time.Instant
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DataJpaTest
@ContextConfiguration(classes = [JpaConfig::class])
@DisplayName("파트너저장소와수수료정책저장소연동테스트")
class PartnerRepositoryPolicyRepositoryTest @Autowired constructor(
    val feePolicyRepository: FeePolicyJpaRepository,
) {

    @ParameterizedTest
    @MethodSource("findPolicyProvider")
    @DisplayName("지정 시점 이전/동일한 정책 중 가장 최근 것을 반환한다.")
    fun `정책 중 가장 최근것은 반환`(at: Instant, foundId: Long) {
        // Given
        feePolicyRepository.save(
            FeePolicyEntity(
                partnerId = 1L,
                effectiveFrom = Instant.parse("2024-06-01T00:00:00Z"),
                percentage = BigDecimal("0.0300"),
                fixedFee = BigDecimal("300")
            )
        )

        // When
        val foundPolicy = feePolicyRepository.findTop1ByPartnerIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            partnerId = 1L,
            at = at,
        )

        // Then
        assertNotNull(foundPolicy)
        assertEquals(foundPolicy.id, foundId)
    }

    companion object {
        @JvmStatic
        fun findPolicyProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(Instant.parse("2024-06-04T00:00:00Z"), 6L),
            Arguments.of(Instant.now(), 4L)
        )
    }
}
