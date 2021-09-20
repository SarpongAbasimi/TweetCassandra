scalaVersion := "2.13.6"

val http4sVersion            = "0.22.3"
val logBackVersion           = "1.2.5"
val logForCatsVersion        = "1.1.1"
val lofForCatsSlf4jVersion   = "1.1.1"
val circeGenericExtraVersion = "0.14.1"
val pureConfigVersion        = "0.16.0"
val configVersion            = "1.4.1"
val circeLiteralVersion      = "0.14.1"
val phantomDslVersion        = "2.50.0"

val root = (project in file(".")).settings(
  name := "twitterCassandra",
  libraryDependencies ++= Seq(
    "org.http4s"            %% "http4s-dsl"           % http4sVersion,
    "org.http4s"            %% "http4s-blaze-server"  % http4sVersion,
    "org.http4s"            %% "http4s-blaze-client"  % http4sVersion,
    "org.http4s"            %% "http4s-circe"         % http4sVersion,
    "ch.qos.logback"         % "logback-classic"      % logBackVersion,
    "io.chrisdavenport"     %% "log4cats-core"        % logForCatsVersion,
    "io.chrisdavenport"     %% "log4cats-slf4j"       % lofForCatsSlf4jVersion,
    "io.circe"              %% "circe-generic-extras" % circeGenericExtraVersion,
    "com.github.pureconfig" %% "pureconfig"           % pureConfigVersion,
    "com.typesafe"           % "config"               % configVersion,
    "io.circe"              %% "circe-literal"        % circeLiteralVersion
//    "com.outworkers"        %% "phantom-dsl"          % phantomDslVersion
  )
)
