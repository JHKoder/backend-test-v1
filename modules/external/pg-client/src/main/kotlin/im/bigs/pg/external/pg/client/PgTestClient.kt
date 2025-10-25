package im.bigs.pg.external.pg.client

import com.fasterxml.jackson.databind.ObjectMapper
import im.bigs.pg.application.pg.port.out.PgApproveRequest
import im.bigs.pg.application.pg.port.out.PgApproveResult
import im.bigs.pg.application.pg.port.out.PgClientOutPort
import im.bigs.pg.common.exception.ApiException
import im.bigs.pg.common.exception.ErrorCode
import im.bigs.pg.external.pg.client.`in`.PgApiKeyRequest
import im.bigs.pg.external.pg.client.`in`.PgCreditCardErrorRequest
import im.bigs.pg.external.util.SecurePaymentEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PgTestClient(
    private val webClient: WebClient,
    private val mapper: ObjectMapper,
    @Value("\${pg.test.uri}") private val baseUri: String,
    @Value("\${pg.test.api-key}") private val apiKey: String,
    @Value("\${pg.test.iv}") private val iv: String,
) : PgClientOutPort {

    override fun supports(pgType: String): Boolean {
        return pgType.uppercase() == PgClientType.TEST.name
    }

    override fun approve(request: PgApproveRequest): PgApproveResult {
        val responseEnc = SecurePaymentEncoder.encryptToEnc(apiKey, iv, PgApiKeyRequest.fakeCardOf(request))
        val result = webClient.post()
            .uri("$baseUri/api/v1/pay/credit-card")
            .header("API-KEY", apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("enc" to responseEnc))
            .retrieve()
            .toEntity(String::class.java)
            .block() ?: throw ApiException(ErrorCode.PG_NO_RESPONSE)

        if (result.statusCode.is2xxSuccessful) {
            return mapper.readValue(result.body, PgApproveResult::class.java)
        }

        val error = mapper.readValue(result.body, PgCreditCardErrorRequest::class.java)
        throw ApiException(ErrorCode.findApyKeyName(error.errorCode))
    }
}