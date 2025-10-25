package im.bigs.pg.common.handler

import im.bigs.pg.common.exception.ApiException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiErrorValidResponse> {
        val message = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "잘못된 요청입니다."

        return ResponseEntity
            .status(400)
            .body(ApiErrorValidResponse(message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleValidationException(e: IllegalArgumentException): ResponseEntity<ApiErrorValidResponse> {
        val message = e.message ?: "잘못된 요청입니다."

        return ResponseEntity
            .status(400)
            .body(ApiErrorValidResponse(message))
    }

    @ExceptionHandler(ApiException::class)
    fun handleValidationException(e: ApiException): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse.from(e.errorCode)
        log.warn("API error code : ${response.code} (message=${response.message})")

        return ResponseEntity.status(response.code)
            .body(response)
    }
}
