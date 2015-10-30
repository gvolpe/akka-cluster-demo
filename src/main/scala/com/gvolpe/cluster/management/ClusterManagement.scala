package com.gvolpe.cluster.management

import akka.actor.ActorSystem
import akka.cluster.sharding.{ClusterSharding, ShardRegion}
import com.gvolpe.cluster.actors.EntityActor

class ClusterManagement(system: ActorSystem, port: Int) extends ClusterManagementMBean {

  override def leaveClusterAndShutdown(): Unit = {
    println(s"INVOKING MBEAN ${system.name}")

    val region = ClusterSharding(system).shardRegion(EntityActor.shardName)
    region ! ShardRegion.GracefulShutdown
  }

}
