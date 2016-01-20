package com.gvolpe.cluster.metrics

import akka.actor.{Props, ActorLogging, Actor}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.metrics.ClusterMetricsChanged
import akka.cluster.metrics.StandardMetrics.{Cpu, HeapMemory}
import akka.cluster.metrics.{NodeMetrics, ClusterMetricsExtension}

object MetricsListener {
  def props = Props[MetricsListener]
}

class MetricsListener extends Actor with ActorLogging {

  val selfAddress = Cluster(context.system).selfAddress
  val extension = ClusterMetricsExtension(context.system)

  override def preStart(): Unit = extension.subscribe(self)

  override def postStop(): Unit = extension.unsubscribe(self)

  def receive = {
    case ClusterMetricsChanged(clusterMetrics) =>
      clusterMetrics filter (_.address == selfAddress) foreach { metrics =>
        logHeap(metrics)
        logCpu(metrics)
      }
    case state: CurrentClusterState =>
      log.info(s"Leader Node: ${state.getLeader}")
  }

  def logHeap(nodeMetrics: NodeMetrics): Unit = nodeMetrics match {
    case HeapMemory(address, timestamp, used, committed, max) =>
      log.info("Used heap: {} MB", used.doubleValue / 1024 / 1024)
    case _ => // No heap info.
  }

  def logCpu(nodeMetrics: NodeMetrics): Unit = nodeMetrics match {
    case Cpu(address, timestamp, Some(systemLoadAverage), cpuCombined, cpuStolen, processors) =>
      log.info("Load: {} ({} processors)", systemLoadAverage, processors)
    case _ => // No cpu info.
  }

}
