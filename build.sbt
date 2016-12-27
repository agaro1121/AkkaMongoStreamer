name := "AkkaMongoStreamer"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.1"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.1"