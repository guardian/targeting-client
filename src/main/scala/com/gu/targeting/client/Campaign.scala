package com.gu.targeting.client

import java.util.UUID
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.HttpClients
import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.amazonaws.services.dynamodbv2.document.Item
import org.joda.time.DateTime
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
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

  def fromJson(json: JsValue) = {
    json.as[Campaign]
  }

  def toJson(campaign: Campaign): JsValue = {
    Json.toJson[Campaign](campaign)
  }

  def toJson(campaigns: List[Campaign]): JsValue = {
    Json.toJson[List[Campaign]](campaigns)
  }

  def fromItem(item: Item): Campaign = {
    Json.parse(item.toJSON).as[Campaign]
  }

  def toItem(campaign: Campaign): Item = {
    Item.fromJSON(Json.toJson(campaign).toString())
  }
}

case class CampaignCache(campaigns: List[Campaign], totalCampaigns: Option[Int]) {
  def getCampaignsForTags(tags: Seq[String]): List[Campaign] = {
    campaigns.filter(c => c.rules.exists(r => r.evaluate(tags)))
  }

  def getFieldType(campaign: Campaign): Option[String] = {
    campaign.fields match {
      case _: EmailFields => Some(Fields.emailType)
      case _ => None
    }
  }
}

object CampaignCache {
  val TOTAL_CAMPAIGNS_HEADER_NAME = "Total-Campaigns"

  /// Fetch a new campaign cache which contains the latest campaigns.
  /// The URL should correspond to the api end point which lists campaigns as json.
  /// For example: https://targeting.gutools.co.uk/api/campaigns
  def fetch(url: String, limit: Int = 100): Future[CampaignCache] = {
    val queryUrl = url + s"?activeOnly=true&limit=$limit&types=${Fields.allTypes.mkString(",")}"

    Future {
      val response = HttpClients.createDefault().execute(new HttpGet(queryUrl))

      val status = response.getStatusLine.getStatusCode
      if (!(200 to 299).contains(status)) {
        throw TargetingServiceException(s"Failed to get campaign list, status code: $status")
      }

      val body = Source.fromInputStream(response.getEntity.getContent).getLines().mkString("")
      val header = response.getFirstHeader(TOTAL_CAMPAIGNS_HEADER_NAME)
      val totalCampaigns = Try(header.getValue.toInt).toOption

      CampaignCache(Json.parse(body).as[List[Campaign]], totalCampaigns)
    }
  }
}
