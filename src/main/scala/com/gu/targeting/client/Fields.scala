package com.gu.targeting.client

import play.api.libs.json._
import play.api.libs.functional.syntax._

trait Fields

case class EmailFields(name: String, theme: String, about: String, description: String, frequency: String, listId: String) extends Fields
case class BadgeFields(seriesTag: String, imageUrl: String, classModifier: Option[String]) extends Fields
case class EpicFields(campaignId: String) extends Fields
case class ReportFields(campaignId: String) extends Fields
case class SurveyFields(campaignId: String, questions: Seq[SurveyQuestion]) extends Fields
case class TipFields(callout: String, formId: Int) extends Fields

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
  val tipType = "tip"

  val allTypes = List(emailType, badgeType, epicType, reportType, surveyType, tipType)

  val badgeFormat = Json.format[BadgeFields]

  val emailFormat = Json.format[EmailFields]

  val epicFormat = Json.format[EpicFields]

  val reportFormat = Json.format[ReportFields]

  implicit val questionFormat = Json.format[SurveyQuestion]
  
  val surveyFormat = Json.format[SurveyFields]

  val tipFormat = Json.format[TipFields]

  val fieldWrites = new Writes[Fields] {
    override def writes(field: Fields): JsValue = {
      field match {
        case f: EmailFields => emailFormat.writes(f) + (reservedTypeField, JsString(emailType))
        case f: BadgeFields => badgeFormat.writes(f) + (reservedTypeField, JsString(badgeType))
        case f: EpicFields => epicFormat.writes(f) + (reservedTypeField, JsString(epicType))
        case f: ReportFields => reportFormat.writes(f) + (reservedTypeField, JsString(reportType))
        case f: SurveyFields => surveyFormat.writes(f) + (reservedTypeField, JsString(surveyType))
        case f: TipFields => tipFormat.writes(f) + (reservedTypeField, JsString(tipType))
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
        case JsString(`tipType`) => tipFormat.reads(json)
        case other => JsError(s"Unexpected step type value: $other")
      }
    }
  }

  implicit val fieldFormat = Format(fieldReads, fieldWrites)
}
