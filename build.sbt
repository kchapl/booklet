name := "booklet"

ThisBuild / scalaVersion := "2.13.5"

val doobieVersion = "0.12.1"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"                 % "1.0.5",
      "dev.zio"               %% "zio-interop-cats"    % "2.3.1.0",
      "com.squareup.okhttp3"   % "okhttp"              % "4.9.1",
      "com.lihaoyi"           %% "upickle"             % "1.3.0",
      "com.github.pureconfig" %% "pureconfig"          % "0.14.1",
      "org.tpolecat"          %% "doobie-core"         % doobieVersion,
      "com.google.api-client"  % "google-api-client"   % "1.31.3",
      "com.vmunier"           %% "scalajs-scripts"     % "1.1.4",
      "org.typelevel"         %% "munit-cats-effect-2" % "0.13.1"      % Test,
      "org.tpolecat"          %% "doobie-scalatest"    % doobieVersion % Test
    )
  )
