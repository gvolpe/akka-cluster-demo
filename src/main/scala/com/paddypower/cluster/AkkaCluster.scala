package com.paddypower.cluster

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.Cluster
import akka.contrib.pattern.ShardCoordinator.LeastShardAllocationStrategy
import akka.contrib.pattern.{ClusterSharding, ClusterSingletonManager}
import com.paddypower.cluster.actors.{ProcessorGuardian, SharedActor}
import com.typesafe.config.ConfigFactory

class AkkaCluster(name: String, port: Int) {

  val actorSystem: ActorSystem = {
    val portConfig =
      s""" akka.remote.netty.tcp.port=${port}
         | akka.cluster.seed-nodes = ["akka.tcp://$name@127.0.0.1:$port"]
       """.stripMargin
    val config = ConfigFactory.parseString(portConfig).withFallback(ConfigFactory.load())
    val system = ActorSystem(name, config)

    ClusterSharding(system).start(
      typeName = name,
      entryProps = Some(SharedActor.props("shared-dc1")),
      idExtractor = SharedActor.idExtractor,
      shardResolver = SharedActor.shardResolver,
      allocationStrategy = new LeastShardAllocationStrategy(2, 1)
    )

    system.actorOf(ClusterSingletonManager.props(
      singletonProps = ProcessorGuardian.props,
      singletonName = s"processorGuardian-$name",
      terminationMessage = Stop,
      role = Some("processor")
    ))

    system
  }

  def actor(props: Props) = actorSystem.actorOf(props)

  def awaitTermination = actorSystem.awaitTermination()

  def leaveCluster = {
    val address = Address("akka.tcp", name, "127.0.0.1", port)
    Cluster(actorSystem).leave(address)
    Cluster(actorSystem).down(address)
  }

}