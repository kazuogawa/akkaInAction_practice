import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

class SilentActor01Test extends TestKit(ActorSystem("testsystem"))
  //BDDスタイルのテストができるように可読性の高いDSLを提供する
  with WordSpecLike
  //可読性の高いアサーションを提供
  with MustMatchers
  with StopSystemAfterAll {
  //SilentActor
  "A Silent Actor" must {
    //メッセージ受信時の状態変更(シングルスレッド)
    "change state when it receives a message, single threaded" in {
      //テストを書くと最初は失敗する？
      fail("not implemented yet")
    }
    //メッセージ受信時の状態変更(マルチスレッド)
    "change state when it receives a message, multi threaded" in {
      //最初は失敗
      fail("not implement yet")
    }
  }
}
