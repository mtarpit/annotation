name := """annotation"""
organization := "com.mindtickle"

version := "avengers"


lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "org.mockito" % "mockito-core" % "2.10.0" % "test"

libraryDependencies += "com.mindtickle.api" % "mt-api-sdk" % "2.annotation"

 resolvers += (
  "Local Maven Repository" at "file:////Users/arpitgoyal/.m2/repository"
  )

libraryDependencies ++= Seq(
 "com.couchbase.client" % "couchbase-client" % "1.4.9",
 "com.couchbase.client" % "java-client" % "2.3.3"
)