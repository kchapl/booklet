name := "booklet"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.4"

val doobieVersion = "0.9.0"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig"       % "0.14.0",
  "org.tpolecat"          %% "doobie-core"      % doobieVersion,
  "org.tpolecat"          %% "doobie-postgres"  % doobieVersion,
  "org.tpolecat"          %% "doobie-scalatest" % doobieVersion % Test
)

TwirlKeys.templateImports := Seq()
