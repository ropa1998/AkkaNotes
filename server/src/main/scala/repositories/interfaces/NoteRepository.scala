package repositories.interfaces

import model.Note

trait NoteRepository {

  def listAll(): List[Note]

  def save(title: String, body: String)

  def delete(id: Long)

  def update(id: Long, title: String, body: String)


}
