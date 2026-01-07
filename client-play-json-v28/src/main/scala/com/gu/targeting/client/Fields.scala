package com.gu.targeting.client

import play.api.libs.json._
import play.api.libs.functional.syntax._

trait Fields

case class EmailFields(name: String, theme: String, about: String, description: String, frequency: String, listId: String) extends Fields
case class BadgeFields(seriesTag: String, imageUrl: String, classModifier: Option[String]) extends Fields
case class EpicFields(campaignId: String) extends Fields
case class ReportFields(campaignId: String) extends Fields
case class SurveyFields(campaignId: String, questions: Seq[SurveyQuestion]) extends Fields
// 'Participation' is more commonly known as a 'community callout' outside of this project
case class ParticipationFields(callout: String, formId: Int, tagName: String, description: Option[String], formFields: JsValue, formUrl: Option[String], contacts: Option[Seq[Contact]]) extends Fields
case class ReporterCalloutFields(title: String, subtitle: String, intro: String, mainText: String, messagingContacts: Seq[Contact], emailContacts: Seq[EmailContact], securedropContact: Option[String]) extends Fields

case class EmailContact(name: String, text: String)
case class Contact(name: String, value: String, urlPrefix: String, guidance: Option[String])
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
  val participationType = "callout"
  val reporterCalloutType = "reporter-callout"

  val allTypes = List(emailType, badgeType, epicType, reportType, surveyType, participationType, reporterCalloutType)

  val badgeFormat = Json.format[BadgeFields]

  val emailFormat = Json.format[EmailFields]

  val epicFormat = Json.format[EpicFields]

  val reportFormat = Json.format[ReportFields]

  implicit val contactFormat = Json.format[Contact]
  implicit val emailContactFormat = Json.format[EmailContact]

  implicit val questionFormat = Json.format[SurveyQuestion]
  
  val surveyFormat = Json.format[SurveyFields]

  val participationFormat = Json.format[ParticipationFields]

  val reporterCalloutFormat = Json.format[ReporterCalloutFields]

  val fieldWrites = new Writes[Fields] {
    override def writes(field: Fields): JsValue = {
      field match {
        case f: EmailFields => emailFormat.writes(f) + (reservedTypeField, JsString(emailType))
        case f: BadgeFields => badgeFormat.writes(f) + (reservedTypeField, JsString(badgeType))
        case f: EpicFields => epicFormat.writes(f) + (reservedTypeField, JsString(epicType))
        case f: ReportFields => reportFormat.writes(f) + (reservedTypeField, JsString(reportType))
        case f: SurveyFields => surveyFormat.writes(f) + (reservedTypeField, JsString(surveyType))
        case f: ParticipationFields => participationFormat.writes(f) + (reservedTypeField, JsString(participationType))
        case f: ReporterCalloutFields => reporterCalloutFormat.writes(f) + (reservedTypeField, JsString(reporterCalloutType))
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
        case JsString(`participationType`) => participationFormat.reads(json)
        case JsString(`reporterCalloutType`) => reporterCalloutFormat.reads(json)
        case other => JsError(s"Unexpected step type value: $other")
      }
    }
  }

  implicit val fieldFormat = Format(fieldReads, fieldWrites)
}
