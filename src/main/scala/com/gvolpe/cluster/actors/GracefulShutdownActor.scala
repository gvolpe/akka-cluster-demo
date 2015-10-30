package com.gvolpe.cluster.actors

import akka.actor.{Terminated, Props, Actor}
import akka.cluster.Cluster
import akka.cluster.sharding.{ClusterSharding, ShardRegion}
import com.gvolpe.cluster.actors.GracefulShutdownActor.LeaveAndShutdownNode

object GracefulShutdownActor {
  case object LeaveAndShutdownNode
  def props = Props[GracefulShutdownActor]
}

class GracefulShutdownActor extends Actor {

  val cluster = Cluster(context.system)
  val region = ClusterSharding(context.system).shardRegion(EntityActor.shardName)

  def receive = {
    case LeaveAndShutdownNode =>
      context.watch(region)
      region ! ShardRegion.GracefulShutdown
    case Terminated(`region`) =>
      cluster.registerOnMemberRemoved(context.system.terminate())
      cluster.leave(cluster.selfAddress)
  }

}
