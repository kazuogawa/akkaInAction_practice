package actortest

import actortest.SendingActor.{Event, SortEvents, SortedEvents}
import actortest.SilentActor.{GetState, SilentMessage}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.util.Random

class SendingActorTest extends TestKit(ActorSystem("testsystem"))
  //BDDスタイルのテストができるように可読性の高いDSLを提供する
  with WordSpecLike
  //可読性の高いアサーションを提供
  with MustMatchers
  with StopSystemAfterAll{
 "A Sending Actor" must {
    //処理終了後に別アクターへメッセージを送信する
    "send a message to another actor when it has finished processing" in {
      //受信者となるアクターをpropsメソッドで渡し(今回の場合だとtestActor),propsを作成する。
      val props = SendingActor.props(testActor)
      val sendingActor = system.actorOf(props, "sendingActor")
      val size = 1000
      val maxInclusive = 100000
      def randomEvents = (0 until size).map{_ =>
        Event(Random.nextInt(maxInclusive))
      }.toVector
      val unsorted: Vector[Event] = randomEvents
      val sortEvents: SortEvents = SortEvents(unsorted)
      sendingActor ! sortEvents
      expectMsgPF(){
        case SortedEvents(events) =>
          events.size must be(size)
          unsorted.sortBy(_.id) must be (events)
      }
    }
  }
}


