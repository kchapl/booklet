name := "booklet"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.4"

val doobieVersion = "0.9.0"

libraryDependencies ++= Seq(
  "dev.zio"               %% "zio"                 % "1.0.3",
  "dev.zio"               %% "zio-interop-cats"    % "2.2.0.1",
  "com.squareup.okhttp3"   % "okhttp"              % "4.9.0",
  "com.lihaoyi"           %% "upickle"             % "0.9.5",
  "com.github.pureconfig" %% "pureconfig"          % "0.14.0",
  "org.tpolecat"          %% "doobie-core"         % doobieVersion,
  "org.tpolecat"          %% "doobie-postgres"     % doobieVersion,
  "com.google.api-client"  % "google-api-client"   % "1.30.4",
  "org.typelevel"         %% "munit-cats-effect-2" % "0.11.0"      % Test,
  "org.tpolecat"          %% "doobie-scalatest"    % doobieVersion % Test
)

TwirlKeys.templateImports := Seq()
