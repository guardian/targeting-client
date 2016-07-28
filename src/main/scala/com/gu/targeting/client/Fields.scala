package targeting.client

import org.cvogt.play.json.Jsonx
import play.api.libs.json._

trait Fields

case class EmailFields(name: String, theme: String, about: String, description: String, frequency: String, listId: String) extends Fields

// Add more fields here as applicable

object Fields {
  // Special field the serializer uses to transfer information about the type of the fields across the typeless JSON
  val reservedTypeField = "__type"

  // List of reserved type names used by the '__type' field to select the correct subclass of Fields
  val emailType = "email"

  val emailFormat = Jsonx.formatCaseClassUseDefaults[EmailFields]

  val fieldWrites = new Writes[Fields] {
    override def writes(field: Fields): JsValue = {
      field match {
        case f: EmailFields => emailFormat.writes(f).asInstanceOf[JsObject] + (reservedTypeField, JsString(emailType))
        case other => {
          throw new UnsupportedOperationException(s"Unable to serialize field of type ${other.getClass}")
        }
      }
    }
  }

  val fieldReads = new Reads[Fields] {
    override def reads(json: JsValue): JsResult[Fields] = {
      (json \ reservedTypeField).get match {
        case JsString(emailType) => emailFormat.reads(json)
        case other => JsError(s"Unexpected step type value: ${other}")
      }
    }
  }

  implicit val fieldFormat = Format(fieldReads, fieldWrites)
}
