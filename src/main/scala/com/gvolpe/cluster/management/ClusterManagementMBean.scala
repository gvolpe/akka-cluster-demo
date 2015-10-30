package com.gvolpe.cluster.management

trait ClusterManagementMBean {
  def leaveClusterAndShutdown(): Unit
}
