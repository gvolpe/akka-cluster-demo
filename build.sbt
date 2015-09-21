name := """akka-cluster-unique-state"""

version := "1.0"

scalaVersion := "2.11.5"

val akkaVersion = "2.3.12"

resolvers += "PP repository" at "http://artifactory.cmdb.inhouse.paddypower.com:8081/artifactory/plugins-release"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.github.krasserm" %% "akka-persistence-cassandra" % "0.4.2",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.6",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "org.scalatest" %% "scalatest" % "2.2.4" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)