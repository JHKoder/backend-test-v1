package im.bigs.pg.common.exception

enum class ErrorCode(val status: Int,  val message: String) {
    //Validation
    INVALID_INPUT(400, "입력 값이 유효하지 않습니다."),

    //HEADER
    HEADER_NOT_API_KEY(401,  "API Key 헤더가 존재하지 않습니다."),
    HEADER_API_KEY_FORMAT(401,  "API Key 헤더 형식이 올바르지 않습니다."),
    HEADER_UNREGISTERED_API_KEY(401,  "등록되지 않은 API Key 입니다."),

    //API-KEY
    STOLEN_OR_LOST(422,  "요청 데이터가 유효하지 않습니다."),
    INSUFFICIENT_LIMIT(422, "복호화에 실패했습니다."),
    EXPIRED_OR_BLOCKED(422,  "결제 처리 중 오류가 발생했습니다."),
    TAMPERED_CARD(422,"서버 내부 오류가 발생했습니다."),

    //PG
    PG_NO_RESPONSE(422,  "외부 PG사로부터 응답이 없습니다."),
    PG_CLIENT_NOT_FOUND(402,"해당 파트너사에 대한 PG 클라이언트를 찾을 수 없습니다."),

    //partner
    PARTNER_NOT_FOUND(402, "파트너사를 찾을 수 없습니다."),
    PARTNER_FEE_POLICY(402, "파트너사의 수수료 정책을 찾을 수 없습니다."),
    PAYMENT_QUERY_NOT_STATUS(402, "유효하지 않은 결제 상태입니다.");

    companion object {
        fun findApyKeyName(name: String): ErrorCode {
            return values().firstOrNull { it.name == name } ?: TAMPERED_CARD
        }
    }
}