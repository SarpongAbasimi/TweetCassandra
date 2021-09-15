package models
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import io.circe.generic.extras.defaults._
import io.circe.generic.extras.semiauto.{
  deriveConfiguredDecoder,
  deriveConfiguredEncoder,
  deriveUnwrappedDecoder,
  deriveUnwrappedEncoder
}
import io.circe.{Decoder, Encoder}
import cats.effect.Sync

object Models {

  final case class BaseUrl(baseUrl: String)         extends AnyVal
  final case class BearerToken(bearerToken: String) extends AnyVal
  final case class TwitterConfig(baseUrl: BaseUrl, bearerToken: BearerToken)

  final case class Id(id: String) extends AnyVal
  object Id {
    implicit val encoder: Encoder[Id] = deriveUnwrappedEncoder[Id]
    implicit val decoder: Decoder[Id] = deriveUnwrappedDecoder[Id]
  }

  final case class Name(name: String) extends AnyVal
  object Name {
    implicit val encoder: Encoder[Name] = deriveUnwrappedEncoder[Name]
    implicit val decoder: Decoder[Name] = deriveUnwrappedDecoder[Name]
  }

  final case class UserName(username: String) extends AnyVal
  object UserName {
    implicit val encoder: Encoder[UserName] = deriveUnwrappedEncoder[UserName]
    implicit val decoder: Decoder[UserName] = deriveUnwrappedDecoder[UserName]
  }

  final case class Data(id: Id, name: Name, username: UserName)
  object Data {
    implicit val encoder: Encoder[Data] = deriveConfiguredEncoder[Data]
    implicit val decoder: Decoder[Data] = deriveConfiguredDecoder[Data]
  }

  final case class TwitterGetUserByUserNameResponseData(data: Data)
  object TwitterGetUserByUserNameResponseData {
    implicit val encoder: Encoder[TwitterGetUserByUserNameResponseData] = deriveConfiguredEncoder
    implicit val decoder: Decoder[TwitterGetUserByUserNameResponseData] = deriveConfiguredDecoder
    implicit def entityEncoder[F[_]: Sync]: EntityEncoder[F, TwitterGetUserByUserNameResponseData] =
      jsonEncoderOf[F, TwitterGetUserByUserNameResponseData]
    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, TwitterGetUserByUserNameResponseData] =
      jsonOf[F, TwitterGetUserByUserNameResponseData]
  }
}
