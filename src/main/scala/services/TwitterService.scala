package services

import algebras.TwitterFollows
import cats.effect.Sync
import models.Models.TwitterGetUserByUserNameResponseData

trait TwitterServiceAlgebra[F[_]] {
  def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData]
}

object TwitterService {
  def imp[F[_]: Sync](twitterFollows: TwitterFollows[F]): TwitterServiceAlgebra[F] =
    new TwitterServiceAlgebra[F] {
      def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData] =
        twitterFollows.getUserByUserName(userName)
    }
}
