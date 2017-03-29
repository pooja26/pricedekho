name := """PriceApplication"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies ++= Seq(
  "org.mongodb.morphia" % "morphia" % "1.3.2",
  "com.google.code.gson" % "gson" % "2.8.0",
  "com.restfb" % "restfb" % "1.6.11",
  "com.typesafe.play" % "play-mailer_2.11" % "5.0.0"

)
