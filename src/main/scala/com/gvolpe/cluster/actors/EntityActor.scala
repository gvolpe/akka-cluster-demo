package com.gvolpe.cluster.actors

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.sharding.ShardRegion
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import com.gvolpe.cluster.actors.EntityActor.{ProcessingState, Message}

import scala.collection.immutable.SortedMap

object EntityActor {

  def props(id: String) = Props(new EntityActor(id))

  val idExtractor: ShardRegion.ExtractEntityId = {
    case message: Message => (message.seqNo.toString, message)
  }
  val shardResolver: ShardRegion.ExtractShardId = {
    case message: Message => {
      val resolver = s"R${message.seqNo}"
      println(s"RESOLVER: $resolver")
      resolver
    } //(math.abs(idFromKey(messageConsumed.key).hashCode) % 100).toString
  }
  val shardName = "Klaster"

  case class Message(seqNo: Int, key: Int)
  private[actors] case class ProcessingState(processing: SortedMap[Int, Message])
}

private[actors] class EntityActor(id: String) extends PersistentActor with ActorLogging {

  log.info(s"CONSTRUCTOR: $id")

  override def preStart(): Unit = {
    log.info(s"PRE START $id")
  }

  override def postStop(): Unit = {
    log.info(s"POST STOP $id")
  }

  var processingState: SortedMap[Int, Message] = SortedMap.empty

  override def receiveRecover: Receive = {
    case msg: Message => updateState(msg)
    case SnapshotOffer(_, snapshot: ProcessingState) =>
      log.info(s"Snapshot Offer: ${snapshot.processing}")
      processingState = snapshot.processing
    case RecoveryCompleted =>
      log.info(s"Recovery Completed: ${processingState}")
    case Stop =>
      log.info(s"Stopping actor $id")
  }

  override def receiveCommand: Receive = {
    case msg: Message => persist(msg)(updateState)
    case Stop => context.stop(self)
    case _ => println("Unknown Command")
  }

  override def persistenceId: String = s"shared-$id"

  private def updateState(msg: Message): Unit = {
    processingState += msg.seqNo -> msg
    log.info(s"Updating state: ${processingState}")
    log.info(s"Cluster State: ${Cluster(context.system).state}")
  }

}
