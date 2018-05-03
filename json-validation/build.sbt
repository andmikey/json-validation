val ScalatraVersion = "2.6.3"

organization := "com.github.mikey"

name := "JSON validation"

version := "0.1.0"

scalaVersion := "2.12.4"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "com.typesafe.play" % "play-json_2.11" % "2.4.6",
  "org.mongodb" %% "casbah" % "3.1.1",
  "org.json4s" %% "json4s-jackson" % "3.5.2",
  "org.json4s" %% "json4s-mongo" % "3.5.2"
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
