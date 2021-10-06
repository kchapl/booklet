// required by Heroku to run "stage" command
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.6")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.31")

addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % "0.2.16")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.3")

addDependencyTreePlugin
