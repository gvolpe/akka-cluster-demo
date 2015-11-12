package com.gvolpe.cluster.persistence

import akka.persistence.cassandra.PluginConfiguration
import akka.persistence.cassandra.journal.CassandraJournal
import com.typesafe.config.Config

class ShardingCoordinatorCassandraJournal extends CassandraJournal with ShardingJournalPluginConfiguration

trait ShardingJournalPluginConfiguration extends PluginConfiguration {
  override def pluginConfig(systemConfig: Config): Config = systemConfig.getConfig("sharding-journal")
    .withFallback(systemConfig.getConfig("cassandra-journal"))
}
