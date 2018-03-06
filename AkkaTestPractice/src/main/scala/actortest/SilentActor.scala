package actortest

import akka.actor.{Actor, ActorRef}

object SilentActor {
  case class SilentMessage(data:String)
  case class GetState(receiver: ActorRef)
}
class SilentActor extends Actor {
  import SilentActor._
  var internalState: Vector[String] = Vector[String]()
  def receive = {
    case SilentMessage(data) =>
      internalState = internalState :+ data
    //内部状態はGetStateメッセージの中のActorRefに送信される
    case GetState(receiver) => receiver ! internalState
  }
  def state = internalState
}

