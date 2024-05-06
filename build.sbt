import sbt.Keys.semanticdbEnabled

name := "booklet"

scalaVersion := "2.13.11"
scalacOptions ++= Seq("-unchecked", "-deprecation", "-Wunused", "-Werror")

// required by sbt-native-packager plugin
enablePlugins(JavaAppPackaging)

val zioVersion = "2.0.15"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "io.d11" %% "zhttp" % "2.0.0-RC11",
  "dev.zio" %% "zio-interop-cats" % "23.0.0.2",
  "dev.zio" %% "zio-json" % "0.5.0",
  "com.github.pureconfig" %% "pureconfig" % "0.17.4",
  "com.lihaoyi" %% "scalatags" % "0.12.0",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
  "org.postgresql" % "postgresql" % "42.6.0" % Runtime,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-mock" % "1.0.0-RC11" % Test,
  "io.d11" %% "zhttp-test" % "2.0.0-RC9" % Test,
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
