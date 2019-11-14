import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object Server extends App {

  //setting up implicit vals to use along the app
  implicit private val system: ActorSystem = ActorSystem("actorSystem")
  implicit private val materializer: ActorMaterializer = ActorMaterializer()
  implicit private val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit private val timeout: Timeout = Timeout(30 seconds)

  //setting up basic actors for my app

}