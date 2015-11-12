package com.gvolpe.cluster.persistence

import akka.persistence.cassandra.PluginConfiguration
import akka.persistence.cassandra.snapshot.CassandraSnapshotStore

class ShardingCoordinatorCassandraSnapshot extends CassandraSnapshotStore with ShardingSnapshotPluginConfiguration

trait ShardingSnapshotPluginConfiguration extends PluginConfiguration {
  override def configurationKey: String = "sharding-snapshot-store"
}
