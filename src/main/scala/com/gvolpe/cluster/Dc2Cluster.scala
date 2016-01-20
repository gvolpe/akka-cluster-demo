package com.gvolpe.cluster

import com.gvolpe.cluster.metrics.MetricsListener

object Dc2Cluster extends App {

  val cluster = new AkkaCluster("dc2", 2552)
  cluster.actor(MetricsListener.props)
  cluster.awaitTermination

}
