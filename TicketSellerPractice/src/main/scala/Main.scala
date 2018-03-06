import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

object Main extends App with RequestTimeout{
  val config = ConfigFactory.load
  val host = config.getString("http.host")
  val port = config.getString("http.port")

  val system = ActorSystem("MainActor")
  //ここにdispatcherが必要っぽい
  val ec = system.dispatcher
  //RestApiは別にclassを自作する必要がある
  val api = new RestApi(system,requestTimeout(config)).routes
}

trait RequestTimeout{
  import scala.concurrent.duration._
  def requestTimeout(config:Config):Timeout = {
    val time = config.getString("akka.http.server.request-timeout")
    //期間を表す。単位は20 second?って書き方？
    val duration = Duration(time)
    FiniteDuration(duration.length, duration.unit)
  }
}