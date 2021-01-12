name := "booklet"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  "org.playframework.anorm" %% "anorm"              % "2.6.8",
  "org.postgresql"          % "postgresql"          % "42.2.18",
  "dev.zio"                 %% "zio"                % "1.0.3",
  "org.scalatestplus.play"  %% "scalatestplus-play" % "5.1.0" % "test"
)

scalafmtOnCompile := true

TwirlKeys.templateImports := Seq()
