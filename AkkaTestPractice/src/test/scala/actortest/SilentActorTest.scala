package actortest

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}
import actortest.SilentActor.{GetState, SilentMessage}

class SilentActorTest extends TestKit(ActorSystem("testsystem"))
  //BDDスタイルのテストができるように可読性の高いDSLを提供する
  with WordSpecLike
  //可読性の高いアサーションを提供
  with MustMatchers
  with StopSystemAfterAll{
  //シングルスレッド
  "change internal state when it receives a message, single" in {
    val silentActor = TestActorRef[SilentActor]
    silentActor ! SilentMessage("whisper")
    silentActor.underlyingActor.state must contain("whisper")
  }
  //マルチスレッド
  "change internal state when it receives a message,multi" in {
    val silentActor = system.actorOf(Props[SilentActor],"s3")
    silentActor ! SilentMessage("whisper1")
    silentActor ! SilentMessage("whisper2")
    silentActor ! GetState(testActor)
    //expectMsgをつかうと、silentActorがもつすべてのデータを保持したVectorを受け取る
    expectMsg(Vector("whisper1", "whisper2"))
  }
}


