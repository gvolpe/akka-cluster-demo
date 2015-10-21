package com.paddypower.cluster

import com.paddypower.cluster.actors.MessageGenerator

object Dc1Cluster extends App {

  val cluster = new AkkaCluster("dc1", 2551)

  cluster.actor(MessageGenerator.props)

//  Thread.sleep(1000 * 20)
//
//  cluster.leaveCluster
  cluster.awaitTermination

}
