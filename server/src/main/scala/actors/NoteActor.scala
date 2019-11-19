package actors

import akka.actor.{Actor, Props}
import akka.http.javadsl.model.ws.Message
import repositories.interfaces.NoteRepository

class NoteNotFound extends Throwable

object NoteActor {

  trait Message

  case object List extends Message

  case class Create(title: String, body: String) extends Message

  case class Delete(id: Long) extends Message

  case class Update(id: Long, title: String, body: String) extends Message

  case class Get(id: Long) extends Message

  def props(noteRepository: NoteRepository): Props = Props(new NoteActor(noteRepository))


}

class NoteActor(noteRepository: NoteRepository) extends Actor {

  import NoteActor._

  override def receive: Receive = {
    case List => noteRepository.listAll()
    case Create(t, b) => noteRepository.save(t, b)
    case Delete(id) => noteRepository.delete(id)
    case Update(id, t, b) => noteRepository.update(id, t, b)
    case Get(id) => noteRepository.listAll().filter(_.id == id)
  }
}
