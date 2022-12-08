import sbt.Keys.semanticdbEnabled

name := "booklet"

scalaVersion := "2.13.10"
scalacOptions ++= Seq("-unchecked", "-deprecation", "-Wunused", "-Werror")

// required by Scalafix
semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision

// required by sbt-native-packager plugin
enablePlugins(JavaAppPackaging)

val zioVersion = "2.0.0"
val zhttpVersion = "2.0.0-RC9"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "io.d11" %% "zhttp" % zhttpVersion,
  "dev.zio" %% "zio-interop-cats" % "3.3.0",
  "dev.zio" %% "zio-json" % "0.3.0",
  "com.github.pureconfig" %% "pureconfig" % "0.17.1",
  "com.lihaoyi" %% "scalatags" % "0.12.0",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
  "org.postgresql" % "postgresql" % "42.4.3" % Runtime,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-mock" % "1.0.0-RC9" % Test,
  "io.d11" %% "zhttp-test" % zhttpVersion % Test,
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
