name := "booklet"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  "com.typesafe.play" %% "anorm" % "2.5.3",
  // "com.typesafe.play" %% "play-slick" % "3.0.3",
  // "com.h2database"         % "h2"                  % "1.4.196",
  "org.postgresql"         % "postgresql"          % "42.2.2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test"
)

scalafmtOnCompile := true

TwirlKeys.templateImports := Seq()
