import akka.actor.{Actor, Props}
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

class BoxOffice(implicit timeout:Timeout) extends Actor {
  import BoxOffice._
  import context._

  def createTicketSeller (name:String) =
    context.actorOf(TicketSeller.props(name), name)

  def receive = {
    case CreateEvent(name,tickets) =>
  }

}