import sbt.Keys.semanticdbEnabled

name := "booklet"

scalacOptions ++= Seq("-deprecation", "-Wunused", "-Xlint:adapted-args")
scalaVersion := "2.13.6"

// required by Scalafix
semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision

// required by sbt-native-packager plugin
enablePlugins(JavaAppPackaging)

val doobieVersion = "0.13.4"

val zioVersion = "2.0.0-M2"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-interop-cats" % "3.1.1.0",
  "org.postgresql" % "postgresql" % "42.2.23",
  "io.d11" %% "zhttp" % "1.0.0.0-RC17",
  "com.lihaoyi" %% "upickle" % "1.4.0",
  "com.github.pureconfig" %% "pureconfig" % "0.16.0",
  "com.lihaoyi" %% "scalatags" % "0.9.4",
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "com.google.api-client" % "google-api-client" % "1.32.1",
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test
)
