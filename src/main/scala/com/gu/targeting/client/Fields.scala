package com.gu.targeting.client

import play.api.libs.json._
import play.api.libs.functional.syntax._

trait Fields

case class EmailFields(name: String, theme: String, about: String, description: String, frequency: String, listId: String) extends Fields

// Add more fields here as applicable

object Fields {
  // Special field the serializer uses to transfer information about the type of the fields across the typeless JSON
  val reservedTypeField = "_type"

  val emailType = "email"
  val emailFormat = (
      (JsPath \ "name").format[String] and
      (JsPath \ "theme").format[String] and
      (JsPath \ "about").format[String] and
      (JsPath \ "description").format[String] and
      (JsPath \ "frequency").format[String] and
      (JsPath \ "listId").format[String]
    )(EmailFields.apply, unlift(EmailFields.unapply))

  val fieldWrites = new Writes[Fields] {
    override def writes(field: Fields): JsValue = {
      field match {
        case f: EmailFields => emailFormat.writes(f) + (reservedTypeField, JsString(emailType))
        case other => {
          throw new UnsupportedOperationException(s"Unable to serialize field of type ${other.getClass}")
        }
      }
    }
  }

  val fieldReads = new Reads[Fields] {
    override def reads(json: JsValue): JsResult[Fields] = {
      (json \ reservedTypeField).get match {
        case JsString(`emailType`) => emailFormat.reads(json)
        case other => JsError(s"Unexpected step type value: $other")
      }
    }
  }

  implicit val fieldFormat = Format(fieldReads, fieldWrites)
}
