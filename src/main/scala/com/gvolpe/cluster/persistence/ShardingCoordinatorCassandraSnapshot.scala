package com.gvolpe.cluster.persistence

import akka.persistence.cassandra.PluginConfiguration
import akka.persistence.cassandra.snapshot.CassandraSnapshotStore
import com.typesafe.config.Config

class ShardingCoordinatorCassandraSnapshot extends CassandraSnapshotStore with ShardingSnapshotPluginConfiguration

trait ShardingSnapshotPluginConfiguration extends PluginConfiguration {
  override def pluginConfig(systemConfig: Config): Config = systemConfig.getConfig("sharding-snapshot-store")
    .withFallback(systemConfig.getConfig("cassandra-snapshot-store"))
}
