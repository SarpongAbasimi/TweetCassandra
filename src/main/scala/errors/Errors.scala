package errors

sealed trait Errors extends Exception with Serializable with Product

final case class GetRequestError(message: Throwable) extends Errors
