package algebras

trait TwitterFollows[F[_]] {
  def getAllUserFollowing(user: Int): F[String]
  def getAllUsersBeingFollowedBy(user: Int): F[String]
}
