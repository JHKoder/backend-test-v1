package im.bigs.pg.api.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import im.bigs.pg.api.payment.`in`.CreatePaymentRequest
import im.bigs.pg.application.payment.port.`in`.PaymentUseCase
import im.bigs.pg.application.payment.port.`in`.QueryPaymentsUseCase
import im.bigs.pg.application.payment.port.out.QueryResult
import im.bigs.pg.domain.payment.Payment
import im.bigs.pg.domain.payment.PaymentStatus
import im.bigs.pg.domain.payment.PaymentSummary
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant

@WebMvcTest(PaymentController::class)
@AutoConfigureRestDocs
class PaymentControllerDocsTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var paymentUseCase: PaymentUseCase

    @MockkBean
    private lateinit var queryPaymentsUseCase: QueryPaymentsUseCase

    @Test
    fun `결제 승인 API 문서화`() {
        // given
        val request = CreatePaymentRequest(
            partnerId = 1L,
            amount = BigDecimal("10000"),
            cardBin = "123456",
            cardLast4 = "1234",
            productName = "테스트 상품"
        )

        val mockPayment = Payment(
            id = 1L,
            partnerId = 1L,
            amount = BigDecimal("10000"),
            appliedFeeRate = BigDecimal("0.03"),
            feeAmount = BigDecimal("300"),
            netAmount = BigDecimal("9700"),
            cardBin = "123456",
            cardLast4 = "1234",
            approvalCode = "APPROVAL123",
            approvedAt = Instant.parse("2025-10-27T00:00:00Z"),
            status = PaymentStatus.APPROVED,
            createdAt = Instant.parse("2025-10-27T00:00:00Z")
        )

        every { paymentUseCase.pay(any()) } returns mockPayment

        // when & then
        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "payment-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("partnerId").type(JsonFieldType.NUMBER).description("파트너 PG ID (1 이상)"),
                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액 (1 이상)"),
                        fieldWithPath("cardBin").type(JsonFieldType.STRING).optional().description("카드 BIN (선택)"),
                        fieldWithPath("cardLast4").type(JsonFieldType.STRING).optional().description("카드 마지막 4자리 (선택)"),
                        fieldWithPath("productName").type(JsonFieldType.STRING).optional().description("상품명 (선택)")
                    ),
                    responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("결제 ID"),
                        fieldWithPath("partnerId").type(JsonFieldType.NUMBER).description("파트너 PG ID"),
                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                        fieldWithPath("appliedFeeRate").type(JsonFieldType.NUMBER).description("적용된 수수료율"),
                        fieldWithPath("feeAmount").type(JsonFieldType.NUMBER).description("수수료 금액"),
                        fieldWithPath("netAmount").type(JsonFieldType.NUMBER).description("정산 금액 (결제금액 - 수수료)"),
                        fieldWithPath("cardLast4").type(JsonFieldType.STRING).description("카드 마지막 4자리"),
                        fieldWithPath("approvalCode").type(JsonFieldType.STRING).description("승인 코드"),
                        fieldWithPath("approvedAt").type(JsonFieldType.STRING).description("승인 시각 (UTC)"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("결제 상태 (APPROVED, CANCELLED 등)"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시각 (UTC)")
                    )
                )
            )
    }

    @Test
    fun `결제 조회 API 문서화`() {
        // given
        val mockPayment = Payment(
            id = 1L,
            partnerId = 1L,
            amount = BigDecimal("10000"),
            appliedFeeRate = BigDecimal("0.03"),
            feeAmount = BigDecimal("300"),
            netAmount = BigDecimal("9700"),
            cardBin = "123456",
            cardLast4 = "1234",
            approvalCode = "APPROVAL123",
            approvedAt = Instant.parse("2025-10-27T00:00:00Z"),
            status = PaymentStatus.APPROVED,
            createdAt = Instant.parse("2025-10-27T00:00:00Z")
        )

        val mockQueryResult = QueryResult(
            items = listOf(mockPayment),
            summary = PaymentSummary(
                count = 1L,
                totalAmount = BigDecimal("10000"),
                totalNetAmount = BigDecimal("9700")
            ),
            nextCursor = null,
            hasNext = false
        )

        every { queryPaymentsUseCase.query(any()) } returns mockQueryResult

        // when & then
        mockMvc.perform(
            get("/api/v1/payments")
                .param("partnerId", "1")
                .param("status", "APPROVED")
                .param("limit", "20")
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "payment-query",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("partnerId").optional().description("파트너 PG ID (선택)"),
                        parameterWithName("status").optional().description("결제 상태 (선택, APPROVED/CANCELLED 등)"),
                        parameterWithName("from").optional().description("조회 시작 시각 (선택, ISO-8601 형식)"),
                        parameterWithName("to").optional().description("조회 종료 시각 (선택, ISO-8601 형식)"),
                        parameterWithName("cursor").optional().description("페이징 커서 (선택)"),
                        parameterWithName("limit").optional().description("조회 개수 (기본값: 20)")
                    ),
                    responseFields(
                        fieldWithPath("items").type(JsonFieldType.ARRAY).description("결제 목록"),
                        fieldWithPath("items[].id").type(JsonFieldType.NUMBER).description("결제 ID"),
                        fieldWithPath("items[].partnerId").type(JsonFieldType.NUMBER).description("파트너 PG ID"),
                        fieldWithPath("items[].amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                        fieldWithPath("items[].appliedFeeRate").type(JsonFieldType.NUMBER).description("적용된 수수료율"),
                        fieldWithPath("items[].feeAmount").type(JsonFieldType.NUMBER).description("수수료 금액"),
                        fieldWithPath("items[].netAmount").type(JsonFieldType.NUMBER).description("정산 금액"),
                        fieldWithPath("items[].cardLast4").type(JsonFieldType.STRING).description("카드 마지막 4자리"),
                        fieldWithPath("items[].approvalCode").type(JsonFieldType.STRING).description("승인 코드"),
                        fieldWithPath("items[].approvedAt").type(JsonFieldType.STRING).description("승인 시각 (UTC)"),
                        fieldWithPath("items[].status").type(JsonFieldType.STRING).description("결제 상태"),
                        fieldWithPath("items[].createdAt").type(JsonFieldType.STRING).description("생성 시각 (UTC)"),
                        fieldWithPath("summary").type(JsonFieldType.OBJECT).description("조회 결과 요약"),
                        fieldWithPath("summary.count").type(JsonFieldType.NUMBER).description("총 결제 건수"),
                        fieldWithPath("summary.totalAmount").type(JsonFieldType.NUMBER).description("총 결제 금액"),
                        fieldWithPath("summary.totalNetAmount").type(JsonFieldType.NUMBER).description("총 정산 금액"),
                        fieldWithPath("nextCursor").type(JsonFieldType.STRING).optional().description("다음 페이지 커서 (없으면 null)"),
                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부")
                    )
                )
            )
    }
}
