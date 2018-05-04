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
  "com.typesafe.play" % "play-json_2.12" % "2.6.9",
  // "com.eclipsesource"  %% "play-json-schema-validator" % "0.9.4"
  "com.github.java-json-tools" % "json-schema-validator" % "2.2.8",
   "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0"

)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
