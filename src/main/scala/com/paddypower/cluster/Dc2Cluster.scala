package com.paddypower.cluster

import com.paddypower.cluster.actors.SharedActor

object Dc2Cluster extends App {

  val cluster = new AkkaCluster("dc2", 2552)
  val shared = cluster.actor(SharedActor.props("dc1"))

  cluster.awaitTermination

}
