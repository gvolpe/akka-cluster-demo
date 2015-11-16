package com.gvolpe.cluster.actors

import akka.actor.{Actor, Props}
import akka.cluster.sharding.ClusterSharding
import com.gvolpe.cluster.actors.MessageGenerator.{Ack, Generate}
import com.gvolpe.cluster.actors.EntityActor.Message

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.forkjoin.ThreadLocalRandom

object MessageGenerator {
  def props = Props[MessageGenerator]
  case object Generate
  case class Ack(node: String, msgKey: Int)
}

private[actors] class MessageGenerator extends Actor {

  val sharedRegion = ClusterSharding(context.system).shardRegion(EntityActor.shardName)

  context.system.scheduler.schedule(5 seconds, 60 seconds, self, Generate)

  def receive: Receive = {
    case Generate =>
      sharedRegion ! randomMessage(1)
      sharedRegion ! randomMessage(2)
    case Ack(node, key) =>
      println(s"********* ACK from $node for msg key $key ************")
  }

  private def randomMessage(key: Int) = {
    val rndValue = ThreadLocalRandom.current().nextInt(1000) + 1
    Message(key, (key * rndValue / 2))
  }

}
