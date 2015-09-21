package com.paddypower.cluster

import com.paddypower.cluster.actors.{MessageGenerator, SharedActor}

object Dc1Cluster extends App {

  val cluster = new AkkaCluster("dc1", 2551)

  val shared = cluster.actor(SharedActor.props("dc1"))
  val generator = cluster.actor(MessageGenerator.props)

  Thread.sleep(1000 * 20)

  cluster.leaveCluster
  cluster.awaitTermination

}
