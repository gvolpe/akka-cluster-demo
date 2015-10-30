name := """akka-cluster-demo"""

version := "1.0"

scalaVersion := "2.11.7"

val akkaVersion = "2.4.0"

resolvers ++= Seq(
  "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven",
  "PP repository" at "http://artifactory.cmdb.inhouse.paddypower.com:8081/artifactory/plugins-release"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.github.krasserm" %% "akka-persistence-cassandra" % "0.4",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.6",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "org.scalatest" %% "scalatest" % "2.2.4" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)