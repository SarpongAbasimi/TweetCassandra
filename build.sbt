scalaVersion := "2.13.6"

val http4sVersion = "0.22.3"

val root = (project in file(".")).settings(
  name := "twitterCassandra",
  libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion
  )
)
