scalaVersion := "2.13.6"

val http4sVersion     = "0.22.3"
val logBackVersion    = "1.2.5"
val logForCatsVersion = "1.1.1"
val lofForCatsSlf4j   = "1.1.1"
val circeGenericExtra = "0.14.1"

val root = (project in file(".")).settings(
  name := "twitterCassandra",
  libraryDependencies ++= Seq(
    "org.http4s"        %% "http4s-dsl"           % http4sVersion,
    "org.http4s"        %% "http4s-blaze-server"  % http4sVersion,
    "org.http4s"        %% "http4s-blaze-client"  % http4sVersion,
    "org.http4s"        %% "http4s-circe"         % http4sVersion,
    "ch.qos.logback"     % "logback-classic"      % logBackVersion,
    "io.chrisdavenport" %% "log4cats-core"        % logForCatsVersion,
    "io.chrisdavenport" %% "log4cats-slf4j"       % lofForCatsSlf4j,
    "io.circe"          %% "circe-generic-extras" % circeGenericExtra
  )
)
