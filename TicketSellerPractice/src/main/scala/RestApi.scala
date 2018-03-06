import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._


class RestApi(system:ActorSystem, timeout:Timeout) extends RestRoutes {
  //なぜわざわざ置き換えている・・・？
  implicit val requestTimeout: Timeout = timeout
  //dispatcherってなに？
  implicit def executionContext: ExecutionContextExecutor = system.dispatcher

  //propってなに？
  def createBoxOffice(): ActorRef = system.actorOf(BoxOffice.props,BoxOffice.name)
}

trait RestRoutes extends BoxOfficeApi with EventMarshalling {
  def routes:Route = eventsRoute ~ eventRoute ~ ticketsRoute
  def eventsRoute: Route = {
    //pathPrefixとは・・・
    pathPrefix("events"){
      pathEndOrSingleSlash{
        get {
          onSuccess(getEvents()) { events =>
            //第二引数付けるとエラーぽいので外す
            //complete(OK, events)
            complete(OK)
          }
        }
      }
    }
  }

  def eventRoute: Route = {
    //pathPrefixってなに？
    //なぜスラッシュ?

    pathPrefix("events" / Segment) { event =>
      pathEndOrSingleSlash {
        post {
          //POST /events/
          //entityってなに・・・asってなに・・・
          entity(as[EventDescription]) { ed =>
            onSuccess(createEvent(event, ed.tickets)) {
              //ここのeventって名前紛らわしくない？
              case BoxOffice.EventCreated(event) =>
                //第二引数付けるとエラーぽいので外す
                //complete(Created,event)
                complete(Created)
              case BoxOffice.EventExists => {
                val error = Error(s"$event event exists already.")
                //第二引数付けるとエラーぽいので外す
                //complete(BadRequest,error)
                complete(BadRequest)
              }
            }
          }
        } ~
          get {
            //Get /events/:event
            onSuccess(getEvent(event)) {
              _.fold(complete(NotFound))(
                //e => complete(OK, e)
                e => complete(OK)
              )
            }
          } ~
          delete {
            //Delete /events/:event
            onSuccess(getEvent(event)) {
              _.fold(complete(NotFound))(e => complete(OK))
            }
          }
      }
    }
  }

  def ticketsRoute:Route = {
    pathPrefix("events" / Segment / "tickets") { event =>
      post{
        pathEndOrSingleSlash {
          //Post /events/:event/tickets
          entity(as[TicketRequest]){ request =>
            onSuccess(requestTickets(event,request.tickets)){tickets =>
              if(tickets.entries.isEmpty) complete(NotFound)
              else complete(Created)
            }
          }
        }
      }
    }
  }
}

trait BoxOfficeApi {
  import BoxOffice._
  def createBoxOffice(): ActorRef

  implicit def executionContext:ExecutionContext
  implicit def requestTimeout:Timeout

  lazy val boxOffice = createBoxOffice()

  def createEvent(event:String,nrOfTickets:Int) =
    //askってなに・・
    boxOffice.ask(CreateEvent(event,nrOfTickets)).mapTo[EventResponce]

  def getEvents() = boxOffice.ask(GetEvents).mapTo[Events]
  def getEvent(event:String) = boxOffice.ask(GetEvent(event)).mapTo[Option[Event]]

  def cancelEvent(event:String) = boxOffice.ask(CancelEvent(event)).mapTo[Option[Event]]

  def requestTickets(event:String,tickets:Int) = {
    boxOffice.ask(GetTickets(event,tickets)).mapTo[TicketSeller.Tickets]
  }
}
