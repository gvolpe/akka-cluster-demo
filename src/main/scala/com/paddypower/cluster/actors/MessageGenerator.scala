package com.paddypower.cluster.actors

import akka.actor.{Actor, Props}
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

  val sharedActor = context.actorOf(SharedActor.props("dc1"))

  context.system.scheduler.schedule(5 seconds, 3 seconds, self, Generate)

  def receive: Receive = {
    case Generate =>
      sharedActor ! randomMessage
  }

  private def randomMessage = {
    val rndKey = ThreadLocalRandom.current().nextInt(1000)
    val rndValue = ThreadLocalRandom.current().nextInt(5) + 1
    MessageConsumed(rndKey, (s"key$rndKey" -> (rndKey * rndValue / 2)))
  }

}
