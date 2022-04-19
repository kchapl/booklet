// required by Heroku to run "stage" command
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.6")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.34")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

addDependencyTreePlugin
