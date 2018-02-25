import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
class RestApi(system:ActorSystem, timeout:Timeout) extends RestRoutes {
  //なぜわざわざ置き換えている・・・？
  implicit val requestTimeout = timeout
  //dispatcherってなに？
  implicit def executionContext = system.dispatcher

  //propってなに？
  def createBoxOffice = system.actorOf(BoxOffice.props,BoxOffice.name)
}

trait RestRoutes extends BoxOfficeApi with EventMarshalling {
  def routes:Route = eventsRoute ~ eventRoute ~ ticketRoute

  def eventsRoute = {
    //pathPrefixとは・・・
    pathPrefix("events"){
      pathEndOrSingleSlash{
        get {
          onSuccess(getEvents()) { events =>
            complete(OK,events)
          }
        }
      }
    }
  }

  def eventRoute =
  //なぜスラッシュ?
    pathPrefix("events" / Segment) {event =>
      pathEndOrSingleSlash {
        post {
          entity(as[EventDescription]){ ed =>


          }
        }
      }
    }
}

trait BoxOfficeApi {
  import BoxOffice._
  def createBoxOffice(): ActorRef
  lazy val boxOffice = createBoxOffice()
  def getEvents() = boxOffice.ask(GetEvents).mapTo[Events]
}
