package com.gvolpe.cluster.management

import akka.actor.{ActorSystem, Address}
import akka.cluster.Cluster

class ClusterManagement(system: ActorSystem, port: Int) extends ClusterManagementMBean {

  override def leaveClusterAndShutdown(): Unit = {

    println(s"INVOKING MBEAN ${system.name}")

    val address: Address = Address("akka.tcp", system.name,
      system.settings.config.getString("akka.remote.netty.tcp.hostname"), port)

    println(s"MBEAN ADDRESS >> ${address}")

    Cluster(system).leave(address)
    Cluster(system).down(address)
    system.terminate()
  }

}
