package im.bigs.pg.common.handler

import im.bigs.pg.common.exception.ErrorCode

data class ApiErrorResponse(val code: Int, val message: String) {
    companion object {
        fun from(errorCode: ErrorCode): ApiErrorResponse =
            ApiErrorResponse(
                code = errorCode.status,
                message = errorCode.message
            )
    }
}
