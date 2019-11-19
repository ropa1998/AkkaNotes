import actors.{NoteActor, NoteNotFound}
import actors.NoteActor.Create
import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, path, _}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import akka.pattern.ask
import model.{InsertableNote, Note}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

case class NoteRoutes(actor: ActorRef)(implicit val ec: ExecutionContext, implicit val timeout: Timeout) extends JsonSupport {

  private def saveNoteHandler(note: InsertableNote): Route = {
    handleRepositoryResponse(askRepository(Create(note.title, note.body))) { _ =>
      complete(HttpResponse(StatusCodes.Accepted))
    }
  }

  private def getAllNotesHandler(): Route =
    handleRepositoryResponse(askRepository(NoteActor.List).mapTo[List[Note]])(complete(_))


  private def getNoteByIdHandler(id: Long): Route =
    handleRepositoryResponse(askRepository(NoteActor.Get(id)).mapTo[Option[Note]]) {
      case Some(note) => complete(note)
      case None => complete(HttpResponse(StatusCodes.NotFound))
    }

  private def deleteNoteHandler(id: Long): Route = handleRepositoryResponse(askRepository(NoteActor.Delete(id)).mapTo[Either[Throwable, Unit]]) {
    case Right(_) => complete(HttpResponse(StatusCodes.Accepted))
    case Left(e) => e match {
      case _: NoteNotFound => complete(HttpResponse(StatusCodes.NotFound))
    }
  }

  private def updateNoteHandler(id: Long, note: InsertableNote): Route = handleRepositoryResponse(askRepository(NoteActor.Update(id, note.title, note.body)).mapTo[Either[Throwable, Unit]]) {
    case Right(_) => complete(HttpResponse(StatusCodes.Accepted))
    case Left(e) => e match {
      case _: NoteNotFound => complete(HttpResponse(StatusCodes.NotFound))
    }
  }

  private def askRepository(m: NoteActor.Message) = actor ? m

  private def handleRepositoryResponse[A](f: Future[A])(success: A => Route) = {
    onComplete(f) {
      case Success(v) => success(v)
      case _ => complete(HttpResponse(StatusCodes.InternalServerError))
    }
  }

  def make: Route =
    path("note") {
      post {
        entity(as[InsertableNote]) {
          create => saveNoteHandler(create)
        }
      }
      get {
        getAllNotesHandler()
      }
    } ~ path("notes" / LongNumber) { id =>
      get {
        getNoteByIdHandler(id)
      }
      delete {
        deleteNoteHandler(id)
      }
      put {
        entity(as[InsertableNote]) {
          note =>
            updateNoteHandler(id, note)
        }
      }
    }


}
