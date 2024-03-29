package com.gu.targeting.client

import java.util.UUID

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Try

case class Campaign (
  id: UUID,
  name: String,
  rules: List[Rule],
  priority: Int,
  activeFrom: Option[Long],
  activeUntil: Option[Long],
  displayOnSensitive: Boolean,
  fields: Fields)

object Campaign {
  implicit val campaignFormat = (
    (JsPath \ "id").format[UUID] and
    (JsPath \ "name").format[String] and
    (JsPath \ "rules").format[List[Rule]] and
    (JsPath \ "priority").format[Int] and
    (JsPath \ "activeFrom").formatNullable[Long] and
    (JsPath \ "activeUntil").formatNullable[Long] and
    (JsPath \ "displayOnSensitive").format[Boolean] and
    (JsPath \ "fields").format[Fields]
  )(Campaign.apply, unlift(Campaign.unapply))

  def fromJson(json: JsValue): Campaign = {
    json.as[Campaign]
  }

  def toJson(campaign: Campaign): JsValue = {
    Json.toJson[Campaign](campaign)
  }

  def toJson(campaigns: List[Campaign]): JsValue = {
    Json.toJson[List[Campaign]](campaigns)
  }

  def getFieldType(campaign: Campaign): Option[String] = {
    campaign.fields match {
      case _: EmailFields => Some(Fields.emailType)
      case _: BadgeFields => Some(Fields.badgeType)
      case _: EpicFields => Some(Fields.epicType)
      case _: ReportFields => Some(Fields.reportType)
      case _: SurveyFields => Some(Fields.surveyType)
      case _: ParticipationFields => Some(Fields.participationType)
      case _ => None
    }
  }
}

case class CampaignCache(campaigns: List[Campaign], totalCampaigns: Option[Int]) {
  def getCampaignsForTags(tags: Seq[String], stripRules: Boolean = false): List[Campaign] = {
    campaigns.filter(c => c.rules.exists(r => Rule.evaluate(r, tags))).map { c =>
      if (stripRules) c.copy(rules = Nil) else c
    }
  }
}

object CampaignCache {
  val TOTAL_CAMPAIGNS_HEADER_NAME = "Total-Campaigns"

  /** Fetch a new campaign cache which contains the latest campaigns.
   * @param url should correspond to the api end point which lists campaigns as json (https://targeting.gutools.co.uk/api/campaigns)
   * @param limit truncate the number of campaigns to this number
   * @param ruleLimit Any campaigns with more rules than this number will be dropped from the results
   * @param tagLimit Any campaigns with any rules with more tags (requiredTags + lackingTags) than this number will be dropped
   * @param ec The execution context that will execute the blocking HTTP request
   */
  def fetch(url: String, limit: Int = 100, ruleLimit: Option[Int] = None, tagLimit: Option[Int] = None)(implicit ec: ExecutionContext): Future[CampaignCache] = {
    val queryUrl = url + s"?activeOnly=true&limit=$limit&types=${Fields.allTypes.mkString(",")}"

    Future {
      val response = HttpClients.createDefault().execute(new HttpGet(queryUrl))

      val status = response.getStatusLine.getStatusCode
      if (!(200 to 299).contains(status)) {
        throw TargetingServiceException(s"Failed to get campaign list, status code: $status")
      }

      val body = Source.fromInputStream(response.getEntity.getContent).getLines().mkString("")

      // Total campaigns number, used to indicate to the client how many campaigns they've truncated
      val header = response.getFirstHeader(TOTAL_CAMPAIGNS_HEADER_NAME)
      val totalCampaigns = Try(header.getValue.toInt).toOption

      val campaigns = Json.parse(body).as[List[Campaign]].filter(campaign => {
        // Is the number of rules less than or equal to the limit?
        ruleLimit.forall(campaign.rules.length <= _) &&
        // And all of the rules have too many required or lacking tags
        tagLimit.forall(limit => campaign.rules.forall(rule => rule.requiredTags.length + rule.lackingTags.length <= limit))
      })

      CampaignCache(campaigns, totalCampaigns)
    }
  }
}
