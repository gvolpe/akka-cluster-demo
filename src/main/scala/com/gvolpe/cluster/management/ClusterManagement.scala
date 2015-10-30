package com.gvolpe.cluster.management

import akka.actor.ActorSystem
import com.gvolpe.cluster.actors.GracefulShutdownActor

class ClusterManagement(system: ActorSystem, port: Int) extends ClusterManagementMBean {

  override def leaveClusterAndShutdown(): Unit = {
    println(s"INVOKING MBEAN ${system.name}")

    val shutdownActor = system.actorOf(GracefulShutdownActor.props)
    shutdownActor ! GracefulShutdownActor.LeaveAndShutdownNode
  }

}
