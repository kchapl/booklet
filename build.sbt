import sbt.Keys.semanticdbEnabled

name := "booklet"

scalaVersion := "2.13.8"
scalacOptions ++= Seq("-unchecked", "-deprecation", "-Wunused", "-Werror")

// required by Scalafix
semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision

// required by sbt-native-packager plugin
enablePlugins(JavaAppPackaging)

val zioVersion = "2.0.0-RC5"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-interop-cats" % "3.3.0-RC5",
  "io.d11" %% "zhttp" % "2.0.0-RC7",
  "com.lihaoyi" %% "upickle" % "1.5.0",
  "com.github.pureconfig" %% "pureconfig" % "0.17.1",
  "com.lihaoyi" %% "scalatags" % "0.11.1",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
  "com.squareup.okhttp3" % "okhttp" % "4.9.3",
  "org.postgresql" % "postgresql" % "42.3.4" % Runtime,
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-mock" % "1.0.0-RC5" % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
