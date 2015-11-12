package com.gvolpe.cluster.persistence

import akka.persistence.cassandra.PluginConfiguration
import akka.persistence.cassandra.journal.CassandraJournal

class ShardingCoordinatorCassandraJournal extends CassandraJournal with ShardingJournalPluginConfiguration

trait ShardingJournalPluginConfiguration extends PluginConfiguration {
  override def configurationKey: String = "sharding-journal"
}
