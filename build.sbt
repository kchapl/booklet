//name := "booklet"

ThisBuild / scalaVersion := "2.13.5"

lazy val server = (project in file("server"))
  .enablePlugins(PlayScala)
  .settings(
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    compile in Compile := (compile in Compile).dependsOn(scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"                 % "1.0.5",
      "dev.zio"               %% "zio-interop-cats"    % "2.3.1.0",
      "com.squareup.okhttp3"   % "okhttp"              % "4.9.1",
      "com.lihaoyi"           %% "upickle"             % "1.3.0",
      "com.github.pureconfig" %% "pureconfig"          % "0.14.1",
      "org.tpolecat"          %% "doobie-core"         % doobieVersion,
      "org.tpolecat"          %% "doobie-postgres"     % doobieVersion,
      "com.google.api-client"  % "google-api-client"   % "1.31.3",
      "org.typelevel"         %% "munit-cats-effect-2" % "0.13.1"      % Test,
      "org.tpolecat"          %% "doobie-scalatest"    % doobieVersion % Test
    )
  )

lazy val client = (project in file("client"))
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)

//lazy val root = (project in file("."))
//  .enablePlugins(PlayScala)
//  .enablePlugins(ScalaJSPlugin)

val doobieVersion = "0.12.1"

//libraryDependencies ++= Seq(
//  "dev.zio"               %% "zio"                 % "1.0.5",
//  "dev.zio"               %% "zio-interop-cats"    % "2.3.1.0",
//  "com.squareup.okhttp3"   % "okhttp"              % "4.9.1",
//  "com.lihaoyi"           %% "upickle"             % "1.3.0",
//  "com.github.pureconfig" %% "pureconfig"          % "0.14.1",
//  "org.tpolecat"          %% "doobie-core"         % doobieVersion,
//  "org.tpolecat"          %% "doobie-postgres"     % doobieVersion,
//  "com.google.api-client"  % "google-api-client"   % "1.31.3",
//  "org.scala-js"         %%% "scalajs-dom"         % "1.1.0",
//  "org.typelevel"         %% "munit-cats-effect-2" % "0.13.1"      % Test,
//  "org.tpolecat"          %% "doobie-scalatest"    % doobieVersion % Test
//)

//TwirlKeys.templateImports := Seq()
