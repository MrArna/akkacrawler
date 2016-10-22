name := "hw3"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.11"

libraryDependencies += "org.jgrapht" % "jgrapht-core" % "1.0.0"

parallelExecution in Test := false

mainClass in assembly := Some("edu.uic.cs474.hw3.Main")