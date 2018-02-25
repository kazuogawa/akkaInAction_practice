import spray.json.DefaultJsonProtocol

case class EventDescription(tickets:Int){
  require(tickets > 0)
}


//DefaultJsonProtocolとは・・
trait EventMarshalling extends DefaultJsonProtocol{

}
