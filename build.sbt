name := "booklet"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.h2database"    % "h2"          % "1.4.196"
)

scalafmtOnCompile := true

TwirlKeys.templateImports := Seq()
