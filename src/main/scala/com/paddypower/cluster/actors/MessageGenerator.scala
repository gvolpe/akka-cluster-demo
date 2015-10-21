package com.paddypower.cluster.actors

import akka.actor.{Actor, Props}
import akka.cluster.sharding.ClusterSharding
import com.paddypower.cluster.actors.MessageGenerator.Generate
import com.paddypower.cluster.actors.SharedActor.MessageConsumed

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.forkjoin.ThreadLocalRandom

object MessageGenerator {
  def props = Props[MessageGenerator]
  case object Generate
}

private[actors] class MessageGenerator extends Actor {

  val sharedRegion = ClusterSharding(context.system).shardRegion(SharedActor.shardName)

  context.system.scheduler.schedule(5 seconds, 30 seconds, self, Generate)

  def receive: Receive = {
    case Generate =>
      sharedRegion ! randomMessage(1)
      sharedRegion ! randomMessage(2)
  }

  private def randomMessage(key: Int) = {
    val rndValue = ThreadLocalRandom.current().nextInt(1000) + 1
    MessageConsumed(key, (key * rndValue / 2))
  }

}
