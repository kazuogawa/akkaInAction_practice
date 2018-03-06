import TicketSeller.Add
import akka.actor.{Actor, PoisonPill, Props}

//ticketに関するmessage
object TicketSeller{
  def props(event:String) = Props(new TicketSeller(event))
  case class Ticket(id:Int)
  case class Add(tickets:Vector[Ticket])
  case class Buy(tickets:Int)
  case class Tickets(event:String,entries:Vector[Ticket] = Vector.empty[Ticket])
  case object GetEvent
  case object Cancel
}

//BoxOfficeによって生成される。チケットリストを保持
class TicketSeller(event:String) extends Actor{
  import TicketSeller._
  var tickets = Vector.empty[Ticket]
  def receive = {
    case Add(newTickets)  => tickets = tickets ++ newTickets
    case Buy(nrOfTickets) => {
      val entries: Vector[Ticket] = tickets.take(nrOfTickets)
      if(entries.size >= nrOfTickets) {
        //チケットが足りていれば、取得したチケットを返す
        sender() ! Tickets(event,entries)
        tickets = tickets.drop(nrOfTickets)
      }
      //足りていなかったら、空のチケットメッセージを返す
      //Ticketsクラスのentriesの初期値がemptyのため、空メッセージになる
      else sender() ! Tickets(event)
    }
    //イベント名とチケットの残りの数を返す
    case GetEvent => sender() ! Some(BoxOffice.Event(event,tickets.size))
    case Cancel   => {
      sender() ! Some(BoxOffice.Event(event,tickets.size))
      //poisonpillはキューに終了するメッセージを追加する？
      self ! PoisonPill
    }
  }

}
