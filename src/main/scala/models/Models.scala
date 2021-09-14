package models
import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import io.circe.generic.extras.defaults._
import io.circe.generic.extras.semiauto.{
  deriveConfiguredDecoder,
  deriveConfiguredEncoder,
  deriveUnwrappedDecoder,
  deriveUnwrappedEncoder
}

object Models {
  final case class UserId(userId: Int) extends AnyVal
  object UserId {
    implicit val encoder: Encoder[UserId] = deriveUnwrappedEncoder[UserId]
    implicit val decoder: Decoder[UserId] = deriveUnwrappedDecoder[UserId]
  }
  final case class Title(title: String) extends AnyVal
  object Title {
    implicit val encoder: Encoder[Title] = deriveUnwrappedEncoder[Title]
    implicit val decoder: Decoder[Title] = deriveUnwrappedDecoder[Title]
  }

  final case class DummyData(userId: UserId, title: Title)

  object DummyData {
    implicit val encoder: Encoder[DummyData]                            = deriveConfiguredEncoder[DummyData]
    implicit val decoder: Decoder[DummyData]                            = deriveConfiguredDecoder[DummyData]
    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, DummyData] = jsonOf[F, DummyData]
    implicit def entityEncoder[F[_]: Sync]: EntityEncoder[F, DummyData] =
      jsonEncoderOf[F, DummyData]
  }
}
