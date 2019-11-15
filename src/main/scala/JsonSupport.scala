import java.time._
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import model.{InsertableNote, Note}
import spray.json.{DefaultJsonProtocol, _}

import scala.util.Try

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val localDateFormat: LocalDateFormat.type = LocalDateFormat
  implicit val noteFormat: RootJsonFormat[Note] = jsonFormat3(Note)
  implicit val insertableNoteFormat: RootJsonFormat[InsertableNote] = jsonFormat2(InsertableNote)
}

object LocalDateFormat extends JsonFormat[LocalDate] {

  override def write(obj: LocalDate): JsValue = JsString(formatter.format(obj))

  override def read(json: JsValue): LocalDate = {
    json match {
      case JsString(lDString) =>
        Try(LocalDate.parse(lDString, formatter)).getOrElse(deserializationError(deserializationErrorMessage))
      case _ => deserializationError(deserializationErrorMessage)
    }
  }

  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

  private val deserializationErrorMessage =
    s"Expected date time in ISO offset date time format ex. ${LocalDate.now().format(formatter)}"
}