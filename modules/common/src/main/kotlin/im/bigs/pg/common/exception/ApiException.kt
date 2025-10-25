package im.bigs.pg.common.exception

open class ApiException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
) : RuntimeException(message)
