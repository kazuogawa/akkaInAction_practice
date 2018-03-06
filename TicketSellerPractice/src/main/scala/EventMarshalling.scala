import spray.json.DefaultJsonProtocol

//初期枚数分のイベントのチケットを所持したメッセージ
case class EventDescription(tickets:Int){
  require(tickets > 0)
}

//必要なチケット枚数を保持したメッセージ
case class TicketRequest(tickets:Int){
  require(tickets > 0)
}

case class Error(message: String)

//DefaultJsonProtocolとは・・
trait EventMarshalling extends DefaultJsonProtocol{

}
