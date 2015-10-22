package com.gvolpe.cluster

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.Cluster
import akka.cluster.sharding.ShardCoordinator.LeastShardAllocationStrategy
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.gvolpe.cluster.actors.SharedActor
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

class AkkaCluster(persistentId: String, port: Int) {

  println("AKKA CLUSTER CONSTRUCTOR")

  val actorSystem: ActorSystem = {
    val nodeConfig =
      s""" akka.remote.netty.tcp.port=${port}
       """.stripMargin
    val config = ConfigFactory.parseString(nodeConfig).withFallback(ConfigFactory.load())
    val system = ActorSystem("KlasterSystem", config)

    ClusterSharding(system).start(
      typeName = "Klaster",
      entityProps = SharedActor.props(persistentId),
      settings = ClusterShardingSettings(system),
      extractEntityId = SharedActor.idExtractor,
      extractShardId = SharedActor.shardResolver,
      allocationStrategy = new LeastShardAllocationStrategy(2, 2),
      handOffStopMessage = Stop
    )

//    system.actorOf(ClusterSingletonManager.props(
//      singletonProps = ProcessorGuardian.props,
//      terminationMessage = Stop,
//      settings = ClusterSingletonManagerSettings(system)
//    ))

    system
  }

  def actor(props: Props) = actorSystem.actorOf(props)

  def awaitTermination = Await.result(actorSystem.whenTerminated, 1000 seconds)

  def leaveCluster = {
    val address = Address("akka.tcp", persistentId, "127.0.0.1", port)
    val cluster = Cluster(actorSystem)
    cluster.leave(address)
    cluster.down(address)
  }

}