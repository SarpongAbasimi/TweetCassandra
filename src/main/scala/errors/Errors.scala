package errors

sealed abstract class Errors(message: String, cause: Throwable) extends Throwable(message, cause)

final case class GetRequestError(message: String, error: Throwable) extends Errors(message, error)
