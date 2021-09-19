import sbt.Keys.semanticdbEnabled

name := "booklet"

scalaVersion := "2.13.6"
scalacOptions ++= Seq("-deprecation", "-Wunused", "-Xlint:adapted-args")

// required by Scalafix
semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision

// required by sbt-native-packager plugin
enablePlugins(JavaAppPackaging)

val doobieVersion = "0.13.4"
val zioVersion = "2.0.0-M2"
val pureConfigVersion = "0.16.0"
val upickleVersion = "1.4.0"
val catsVersion = "2.6.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "izumi-reflect" % "2.0.0",
  "dev.zio" %% "zio-interop-cats" % "2.5.1.0",
//  "org.postgresql" % "postgresql" % "42.2.23",
  "io.d11" %% "zhttp" % "1.0.0.0-RC17+57-5bf9d8a8+20210916-2019-SNAPSHOT",
  "io.netty" % "netty-all" % "4.1.68.Final",
  "com.lihaoyi" %% "upickle" % upickleVersion,
  "com.lihaoyi" %% "upickle-core" % upickleVersion,
  "com.lihaoyi" %% "upickle-implicits" % upickleVersion,
  "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-generic" % pureConfigVersion,
  "com.lihaoyi" %% "scalatags" % "0.9.4",
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-free" % doobieVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-free" % catsVersion,
  "org.typelevel" %% "cats-kernel" % catsVersion,
  "org.typelevel" %% "cats-effect" % "2.5.1",
  "com.chuusai" %% "shapeless" % "2.3.7",
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test
)
