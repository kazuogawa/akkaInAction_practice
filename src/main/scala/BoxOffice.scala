import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout

//Event,Ticketの状態を表す？
//
object BoxOffice {
  def props(implicit timeout:Timeout) = Props(new BoxOffice)
  def name: String = "boxOffice"
  case class CreateEvent(name:String, tickets:Int)
  case class GetEvent(name:String)
  //全てのイベントを要求するメッセージ
  case object GetEvents
  case class GetTickets(event:String,tickets:Int)
  //イベントをキャンセルするメッセージ
  case class CancelEvent(name:String)
  case class Event(name:String, tickets: Int)
  case class Events(events: Vector[Event])
  //CreateEventに対して応答するメッセージ
  sealed trait EventResponce
  //Eventが作成されたことを表すメッセージ
  case class EventCreated(event:Event) extends EventResponce
  //イベントが既に存在することを示すメッセージ
  case object EventExists extends EventResponce
}

//TIcketSellerを子アクターとして作成し、リクエストされたイベントに対して
//応答できるようにTicketSellerにチケットの販売を委譲する
class BoxOffice(implicit timeout:Timeout) extends Actor {
  import BoxOffice._
  import context._

  //テスト時にoverride出来るように別メソッドとして定義
  def createTicketSeller (name:String) =
    context.actorOf(TicketSeller.props(name), name)

  def receive = {
    case CreateEvent(name,tickets) => {
      //こうやって関数を中に書くのが普通なのか・・・
      def create = {
        val eventTickets: ActorRef = createTicketSeller(name)
        val newTickets: Vector[TicketSeller.Ticket] = (1 to tickets).map{ ticketId =>
          TicketSeller.Ticket(ticketId)
        }.toVector
        eventTickets ! TicketSeller.Add(newTickets)
        sender() ! EventCreated
      }
      //同名のTicketSellerが作成されていない場合は、create
      //作成されていた場合は、EventExistsを返す
      context.child(name).fold(create)(_ => sender() ! EventExists)
    }
    case GetTickets(event,tickets) => {
      def notFound = sender () ! TicketSeller.Tickets(event)
      def buy(child:ActorRef) = child.forward(TicketSeller.Buy(tickets))
      //TicketSellerが見つからない場合は、空のチケットメッセージを送信
      //TicketSellerが見つかった場合は、TicketSellerから購入
      context.child(event).fold(notFound)(buy)
    }
  }

}