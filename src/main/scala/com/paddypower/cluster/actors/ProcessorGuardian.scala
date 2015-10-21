package com.paddypower.cluster.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.sharding.ClusterSharding
import com.paddypower.cluster.actors.ProcessorGuardian.DestinationHeartbeat

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object ProcessorGuardian {
  case object DestinationHeartbeat
  def props = Props[ProcessorGuardian]
}

private[actors] class ProcessorGuardian extends Actor with ActorLogging {

  context.system.scheduler.scheduleOnce(1 second, self, DestinationHeartbeat)

  val sharedActor = ClusterSharding(context.system).shardRegion(SharedActor.shardName)

  def receive = {
    case DestinationHeartbeat =>

  }
}
