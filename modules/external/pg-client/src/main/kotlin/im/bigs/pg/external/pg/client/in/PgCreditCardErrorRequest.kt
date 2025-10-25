package im.bigs.pg.external.pg.client.`in`

data class PgCreditCardErrorRequest(val code: Int, val errorCode: String, val message: String, val referenceId: String)
