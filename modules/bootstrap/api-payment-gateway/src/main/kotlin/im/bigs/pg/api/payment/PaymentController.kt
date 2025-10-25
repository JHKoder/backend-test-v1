package im.bigs.pg.api.payment

import im.bigs.pg.api.payment.dto.*
import im.bigs.pg.api.payment.`in`.CreatePaymentRequest
import im.bigs.pg.api.payment.out.PaymentResponse
import im.bigs.pg.application.payment.port.`in`.*
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/v1/payments")
@Validated
class PaymentController(
    private val paymentUseCase: PaymentUseCase,
    private val queryPaymentsUseCase: QueryPaymentsUseCase,
) {

    @PostMapping
    fun create(@Valid @RequestBody request: CreatePaymentRequest): ResponseEntity<PaymentResponse> {
        val saved = paymentUseCase.pay(
            PaymentCommand(
                partnerId = request.partnerId,
                amount = request.amount,
                cardBin = request.cardBin,
                cardLast4 = request.cardLast4,
                productName = request.productName,
            ),
        )
        return ResponseEntity.ok(PaymentResponse.from(saved))
    }

    @GetMapping
    fun query(
        @RequestParam(required = false) partnerId: Long?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) from: Instant?,
        @RequestParam(required = false) to: Instant?,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "20") limit: Int,
    ): ResponseEntity<QueryResponse> {
        val res = queryPaymentsUseCase.query(QueryFilter(partnerId, status, from, to, cursor, limit))

        return ResponseEntity.ok(
            QueryResponse(
                items = res.items.map { PaymentResponse.from(it) },
                summary = Summary(res.summary.count, res.summary.totalAmount, res.summary.totalNetAmount),
                nextCursor = res.nextCursor,
                hasNext = res.hasNext,
            ),
        )
    }
}
