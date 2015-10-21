package com.paddypower.cluster.actors

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.sharding.ShardRegion
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import com.paddypower.cluster.actors.SharedActor.{MessageConsumed, ProcessingState}

import scala.collection.immutable.SortedMap

object SharedActor {

  def props(id: String) = Props(new SharedActor(id))

  val idExtractor: ShardRegion.ExtractEntityId = {
    case messageConsumed: MessageConsumed => (messageConsumed.seqNo.toString, messageConsumed)
  }
  val shardResolver: ShardRegion.ExtractShardId = {
    case messageConsumed: MessageConsumed => {
      val resolver = s"R${messageConsumed.seqNo}"
      println(s"RESOLVER: $resolver")
      resolver
    } //(math.abs(idFromKey(messageConsumed.key).hashCode) % 100).toString
  }
  val shardName = "Klaster"

  case class MessageConsumed(seqNo: Int, key: Int)
  private[actors] case class ProcessingState(processing: SortedMap[Int, MessageConsumed])
}

private[actors] class SharedActor(id: String) extends PersistentActor with ActorLogging {

  log.info(s"CONSTRUCTOR: $id")

  override def preStart(): Unit = {
    log.info(s"PRE START $id")
  }

  override def postStop(): Unit = {
    log.info(s"POST STOP $id")
  }

  var processingState: SortedMap[Int, MessageConsumed] = SortedMap.empty

  override def receiveRecover: Receive = {
    case msg: MessageConsumed => updateState(msg)
    case SnapshotOffer(_, snapshot: ProcessingState) =>
      log.info(s"Snapshot Offer: ${snapshot.processing}")
      processingState = snapshot.processing
    case RecoveryCompleted =>
      log.info(s"Recovery Completed: ${processingState}")
    case Stop =>
      log.info(s"Stopping actor $id")
  }

  override def receiveCommand: Receive = {
    case msg: MessageConsumed => persist(msg)(updateState)
    case Stop => context.stop(self)
    case _ => println("Unknown Command")
  }

  override def persistenceId: String = s"shared-$id"

  private def updateState(msg: MessageConsumed): Unit = {
    processingState += msg.seqNo -> msg
    log.info(s"Updating state: ${processingState}")
    log.info(s"Cluster State: ${Cluster(context.system).state}")
  }

}
