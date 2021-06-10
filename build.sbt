name := "booklet"

scalaVersion := "2.13.6"

val doobieVersion = "0.12.1"

val zioVersion = "1.0.9"

libraryDependencies ++= Seq(
  "dev.zio"               %% "zio"                 % zioVersion,
  "dev.zio"               %% "zio-interop-cats"    % "2.5.1.0",
  "org.postgresql"         % "postgresql"          % "42.2.21",
  "io.d11"                %% "zhttp"               % "1.0.0.0-RC17",
  "com.squareup.okhttp3"   % "okhttp"              % "4.9.1",
  "com.lihaoyi"           %% "upickle"             % "1.4.0",
  "com.github.pureconfig" %% "pureconfig"          % "0.16.0",
  "org.tpolecat"          %% "doobie-core"         % doobieVersion,
  "com.google.api-client"  % "google-api-client"   % "1.31.5",
  "com.vmunier"           %% "scalajs-scripts"     % "1.1.4",
  "com.lihaoyi"           %% "scalatags"           % "0.9.4",
  "org.typelevel"         %% "munit-cats-effect-2" % "1.0.3"       % Test,
  "org.tpolecat"          %% "doobie-scalatest"    % doobieVersion % Test,
  "dev.zio"               %% "zio-test"            % zioVersion    % Test,
  "dev.zio"               %% "zio-test-sbt"        % zioVersion    % Test
)

enablePlugins(JavaAppPackaging)
