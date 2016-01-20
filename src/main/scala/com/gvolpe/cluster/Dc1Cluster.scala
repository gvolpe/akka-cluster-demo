package com.gvolpe.cluster

import com.gvolpe.cluster.actors.MessageGenerator
import com.gvolpe.cluster.metrics.MetricsListener

object Dc1Cluster extends App {

  val cluster = new AkkaCluster("dc1", 2551)

  cluster.actor(MessageGenerator.props)
  cluster.actor(MetricsListener.props)

//  Thread.sleep(1000 * 20)
//
//  cluster.leaveCluster
  cluster.awaitTermination

}
