package com.gu.targeting.client

import play.api.libs.json._
import play.api.libs.functional.syntax._

trait Fields

case class EmailFields(name: String, theme: String, about: String, description: String, frequency: String, listId: String) extends Fields
case class BadgeFields(seriesTag: String, imageUrl: String, classModifier: Option[String]) extends Fields
case class EpicFields(campaignId: String) extends Fields
case class ReportFields(campaignId: String) extends Fields
case class SurveyFields(campaignId: String, questions: Seq[SurveyQuestion]) extends Fields

case class SurveyQuestion(question: String, askWhy: Boolean)

// Add more fields here as applicable

object Fields {
  // Special field the serializer uses to transfer information about the type of the fields across the typeless JSON
  val reservedTypeField = "_type"

  val emailType = "email"
  val badgeType = "badge"
  val epicType = "epic"
  val reportType = "report"
  val surveyType = "survey"

  val allTypes = List(emailType, badgeType, epicType, reportType, surveyType)

  val badgeFormat = (
      (JsPath \ "seriesTag").format[String] and
      (JsPath \ "imageUrl").format[String] and
      (JsPath \ "classModifier").formatNullable[String]
    )(BadgeFields.apply, unlift(BadgeFields.unapply))

  val emailFormat = (
      (JsPath \ "name").format[String] and
      (JsPath \ "theme").format[String] and
      (JsPath \ "about").format[String] and
      (JsPath \ "description").format[String] and
      (JsPath \ "frequency").format[String] and
      (JsPath \ "listId").format[String]
    )(EmailFields.apply, unlift(EmailFields.unapply))

  val epicFormat = Json.format[EpicFields]

  val reportFormat = Json.format[ReportFields]

  implicit val questionFormat = Json.format[SurveyQuestion]
  
  val surveyFormat = Json.format[SurveyFields]

  val fieldWrites = new Writes[Fields] {
    override def writes(field: Fields): JsValue = {
      field match {
        case f: EmailFields => emailFormat.writes(f) + (reservedTypeField, JsString(emailType))
        case f: BadgeFields => badgeFormat.writes(f) + (reservedTypeField, JsString(badgeType))
        case f: EpicFields => epicFormat.writes(f) + (reservedTypeField, JsString(epicType))
        case f: ReportFields => reportFormat.writes(f) + (reservedTypeField, JsString(reportType))
        case f: SurveyFields => surveyFormat.writes(f) + (reservedTypeField, JsString(surveyType))
        case other =>
          throw new UnsupportedOperationException(s"Unable to serialize field of type ${other.getClass}")
      }
    }
  }

  val fieldReads = new Reads[Fields] {
    override def reads(json: JsValue): JsResult[Fields] = {
      (json \ reservedTypeField).get match {
        case JsString(`emailType`) => emailFormat.reads(json)
        case JsString(`badgeType`) => badgeFormat.reads(json)
        case JsString(`epicType`) => epicFormat.reads(json)
        case JsString(`reportType`) => reportFormat.reads(json)
        case JsString(`surveyType`) => surveyFormat.reads(json)
        case other => JsError(s"Unexpected step type value: $other")
      }
    }
  }

  implicit val fieldFormat = Format(fieldReads, fieldWrites)
}
