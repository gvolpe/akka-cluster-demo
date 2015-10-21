package com.paddypower.cluster

object Dc2Cluster extends App {

  val cluster = new AkkaCluster("dc2", 2552)
  cluster.awaitTermination

}
