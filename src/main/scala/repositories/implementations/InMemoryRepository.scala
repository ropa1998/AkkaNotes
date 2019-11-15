package repositories.implementations

import model.Note
import repositories.interfaces.NoteRepository

class InMemoryRepository extends NoteRepository {

  var notes: List[Note] = List()

  def uuid = java.util.UUID.randomUUID.toString.toInt

  override def listAll(): List[Note] = {
    notes
  }

  override def save(title: String, body: String): Unit = {
    notes = Note(uuid, title, body) :: notes
  }

  override def delete(id: Long): Unit = {
    notes = notes.filter(_.id != id)
  }

  override def update(id: Long, title: String, body: String): Unit = {
    delete(id)
    save(title, body)
  }
}
