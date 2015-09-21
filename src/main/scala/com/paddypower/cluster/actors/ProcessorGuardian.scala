package com.paddypower.cluster.actors

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorLogging, Props, Actor}

object ProcessorGuardian {
  def props = Props[ProcessorGuardian]
}

private[actors] class ProcessorGuardian extends Actor with ActorLogging {
  def receive = {
    case msg: String =>
      log.info(s"Message received: $msg")
    case Stop => context.stop(self)
    case _ => println("Unknown Message - Guardian")
  }
}
