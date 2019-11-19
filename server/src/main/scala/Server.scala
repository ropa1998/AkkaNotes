import actors.NoteActor
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import repositories.implementations.InMemoryRepository
import repositories.interfaces.NoteRepository

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.io.StdIn

object Server extends App {

  //setting up implicit vals to use along the app
  implicit private val system: ActorSystem = ActorSystem("actorSystem")
  implicit private val materializer: ActorMaterializer = ActorMaterializer()
  implicit private val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit private val timeout: Timeout = Timeout(30 seconds)

  //setting up basic actors for my app
  private val noteRepository: NoteRepository = new InMemoryRepository()
  private val noteActor: ActorRef = system.actorOf(NoteActor.props(noteRepository), "noteRepositoryActor")
  private val routes = new NoteRoutes(noteActor).make
  private val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

  println("Server online at http://localhost:8080\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
