package com.paddypower.cluster.actors

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorLogging, Props}
import akka.cluster.Cluster
import akka.contrib.pattern.ShardRegion
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import com.paddypower.cluster.actors.SharedActor.{MessageConsumed, ProcessingState}

import scala.collection.immutable.SortedMap

object SharedActor {
  def props(name: String) = Props(new SharedActor(name))

  val idExtractor: ShardRegion.IdExtractor = {
    case messageConsumed: MessageConsumed => (idFromKey(messageConsumed.key), messageConsumed)
  }
  val shardResolver: ShardRegion.ShardResolver = {
    case messageConsumed: MessageConsumed => (math.abs(idFromKey(messageConsumed.key).hashCode) % 100).toString
  }

  case class MessageConsumed(seqNo: Int, key: CorrelationKey)
  private[actors] case class ProcessingState(processing: SortedMap[Int, MessageConsumed])

  private[this] def idFromKey(key: CorrelationKey): String = s"${key._1}.${key._2}"
}

private[actors] class SharedActor(name: String) extends PersistentActor with ActorLogging {

  var processingState: SortedMap[Int, MessageConsumed] = SortedMap.empty

  override def receiveRecover: Receive = {
    case msg: MessageConsumed => updateState(msg)
    case SnapshotOffer(_, snapshot: ProcessingState) =>
      log.info(s"Snapshot Offer: ${snapshot.processing}")
      processingState = snapshot.processing
    case RecoveryCompleted =>
      log.info(s"Recovery Completed: ${processingState}")
  }

  override def receiveCommand: Receive = {
    case msg: MessageConsumed => persist(msg)(updateState)
    case Stop => context.stop(self)
    case _ => println("Unknown Command")
  }

  override def persistenceId: String = s"shared-${name}"

  private def updateState(msg: MessageConsumed): Unit = {
    processingState += msg.seqNo -> msg
    log.info(s"Updating state: ${processingState}")
    log.info(s"Cluster State: ${Cluster(context.system).state}")
  }

}
