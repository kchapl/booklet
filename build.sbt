import sbt.Keys.semanticdbEnabled

name := "booklet"

scalaVersion := "2.13.8"
scalacOptions ++= Seq("-unchecked", "-deprecation", "-Wunused", "-Werror")

// required by Scalafix
semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision

// required by sbt-native-packager plugin
enablePlugins(JavaAppPackaging)

val doobieVersion = "1.0.0-RC2"
val zioVersion = "2.0.0-RC2"
val pureConfigVersion = "0.17.1"
val upickleVersion = "1.5.0"
val catsVersion = "2.7.0"
val nettyVersion = "4.1.76.Final"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-stacktracer" % zioVersion,
  "dev.zio" %% "izumi-reflect" % "2.0.8",
  "dev.zio" %% "zio-interop-cats" % "3.3.0-RC2",
  "io.d11" %% "zhttp" % "2.0.0-RC4",
  "io.netty" % "netty-transport" % nettyVersion,
  "io.netty" % "netty-codec-http" % nettyVersion,
  "io.netty" % "netty-common" % nettyVersion,
  "com.lihaoyi" %% "ujson" % upickleVersion,
  "com.lihaoyi" %% "upickle" % upickleVersion,
  "com.lihaoyi" %% "upickle-core" % upickleVersion,
  "com.lihaoyi" %% "upickle-implicits" % upickleVersion,
  "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-generic" % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-generic-base" % pureConfigVersion,
  "com.lihaoyi" %% "scalatags" % "0.11.1",
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-free" % doobieVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-free" % catsVersion,
  "org.typelevel" %% "cats-kernel" % catsVersion,
  "org.typelevel" %% "cats-effect-kernel" % "3.3.11",
  "com.chuusai" %% "shapeless" % "2.3.9",
  "com.squareup.okhttp3" % "okhttp" % "4.9.3",
  "org.postgresql" % "postgresql" % "42.3.3" % Runtime,
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-mock" % "1.0.0-RC2-2" % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
