name := "booklet"

scalaVersion := "2.13.6"

val doobieVersion = "0.12.1"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"                 % "1.0.7",
      "dev.zio"               %% "zio-interop-cats"    % "2.3.1.0",
      "io.d11"                %% "zhttp"               % "1.0.0.0-RC16",
      "com.squareup.okhttp3"   % "okhttp"              % "4.9.1",
      "com.lihaoyi"           %% "upickle"             % "1.3.14",
      "com.github.pureconfig" %% "pureconfig"          % "0.15.0",
      "org.tpolecat"          %% "doobie-core"         % doobieVersion,
      "com.google.api-client"  % "google-api-client"   % "1.31.5",
      "com.vmunier"           %% "scalajs-scripts"     % "1.1.4",
      "org.typelevel"         %% "munit-cats-effect-2" % "1.0.3"       % Test,
      "org.tpolecat"          %% "doobie-scalatest"    % doobieVersion % Test
    )
  )
