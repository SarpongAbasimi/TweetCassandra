package models
import org.http4s.{EntityDecoder, EntityEncoder, QueryParamDecoder}
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher
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

  final case class BaseUrl(baseUrl: String)                                 extends AnyVal
  final case class BearerToken(bearerToken: String)                         extends AnyVal
  final case class TwitterFollowingBaseUrl(twitterFollowingBaseUrl: String) extends AnyVal
  final case class TwitterFollowersBaseUrl(twitterFollowersBaseUrl: String) extends AnyVal
  final case class TwitterConfig(
      baseUrl: BaseUrl,
      bearerToken: BearerToken,
      twitterFollowingBaseUrl: TwitterFollowingBaseUrl,
      twitterFollowersBaseUrl: TwitterFollowersBaseUrl
  )

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

  final case class OptionalMaxResultQueryParamDecoder(optionalMaxResult: String)
  object OptionalMaxResultQueryParamDecoder {
    implicit val decoder: QueryParamDecoder[OptionalMaxResultQueryParamDecoder] =
      QueryParamDecoder[String].map(OptionalMaxResultQueryParamDecoder(_))
  }

  object OptionalMaxResultQueryParamMatcher
      extends OptionalQueryParamDecoderMatcher[OptionalMaxResultQueryParamDecoder]("max_result")

  final case class ProfileImageUrl(profile_image_url: String) extends AnyVal
  object ProfileImageUrl {
    implicit val encoder: Encoder[ProfileImageUrl] = deriveUnwrappedEncoder
    implicit val decoder: Decoder[ProfileImageUrl] = deriveUnwrappedDecoder
  }

  final case class DataWithProfileImageUrl(
      id: Id,
      name: Name,
      username: UserName,
      profile_image_url: ProfileImageUrl
  )

  object DataWithProfileImageUrl {
    implicit val encoder: Encoder[DataWithProfileImageUrl] = deriveConfiguredEncoder
    implicit val decoder: Decoder[DataWithProfileImageUrl] = deriveConfiguredDecoder
  }

  final case class TwitterGetUserByUserNameResponseDataWithProfileUrl(
      data: Seq[DataWithProfileImageUrl]
  )
  object TwitterGetUserByUserNameResponseDataWithProfileUrl {
    implicit val encoder: Encoder[TwitterGetUserByUserNameResponseDataWithProfileUrl] =
      deriveConfiguredEncoder
    implicit val decoder: Decoder[TwitterGetUserByUserNameResponseDataWithProfileUrl] =
      deriveConfiguredDecoder
    implicit def entityEncoder[F[_]: Sync]: EntityEncoder[
      F,
      TwitterGetUserByUserNameResponseDataWithProfileUrl
    ] =
      jsonEncoderOf[F, TwitterGetUserByUserNameResponseDataWithProfileUrl]

    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[
      F,
      TwitterGetUserByUserNameResponseDataWithProfileUrl
    ] = jsonOf[F, TwitterGetUserByUserNameResponseDataWithProfileUrl]
  }

  final case class Ids(ids: Seq[Long]) extends AnyVal
  object Ids {
    implicit val encoder: Encoder[Ids] = deriveUnwrappedEncoder[Ids]
    implicit val decoder: Decoder[Ids] = deriveUnwrappedDecoder[Ids]
  }

  final case class PreviousCursor(previous_cursor: Int) extends AnyVal
  object PreviousCursor {
    implicit val encoder: Encoder[PreviousCursor] = deriveUnwrappedEncoder[PreviousCursor]
    implicit val decoder: Decoder[PreviousCursor] = deriveUnwrappedDecoder[PreviousCursor]
  }

  final case class NextCursor(next_cursor: BigInt) extends AnyVal
  object NextCursor {
    implicit val encoder: Encoder[NextCursor] = deriveUnwrappedEncoder[NextCursor]
    implicit val decoder: Decoder[NextCursor] = deriveUnwrappedDecoder[NextCursor]
  }

  final case class FollowingIds(previous_cursor: PreviousCursor, ids: Ids)
  object FollowingIds {
    implicit val encoder: Encoder[FollowingIds] = deriveConfiguredEncoder[FollowingIds]
    implicit val decoder: Decoder[FollowingIds] = deriveConfiguredDecoder[FollowingIds]
    implicit def entityEncoder[F[_]: Sync]: EntityEncoder[F, FollowingIds] =
      jsonEncoderOf[F, FollowingIds]
    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, FollowingIds] =
      jsonOf[F, FollowingIds]
  }

  final case class FollowersIds(previous_cursor: PreviousCursor, ids: Ids, next_cursor: NextCursor)
  object FollowersIds {
    implicit val encoder: Encoder[FollowersIds] = deriveConfiguredEncoder[FollowersIds]
    implicit val decoder: Decoder[FollowersIds] = deriveConfiguredDecoder[FollowersIds]
    implicit def entityEncoder[F[_]: Sync]: EntityEncoder[F, FollowersIds] =
      jsonEncoderOf[F, FollowersIds]
    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, FollowersIds] =
      jsonOf[F, FollowersIds]
  }
}
