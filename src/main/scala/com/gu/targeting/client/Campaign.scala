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

case class CampaignCache(campaigns: List[Campaign]) {
  def getCampaignsForTags(tags: Seq[String]): List[Campaign] = {
    campaigns.filter(c => c.rules.exists(r => r.evaluate(tags)))
  }
}

object CampaignCache {
  def fetch(url: String): Future[CampaignCache] = {
    Future {
      val response = HttpClients.createDefault().execute(new HttpGet(url))

      val status = response.getStatusLine.getStatusCode
      if (!(200 to 299).contains(status)) {
        throw TargetingServiceException(s"Failed to get campaign list, status code: $status")
      }

      val body = Source.fromInputStream(response.getEntity.getContent).getLines().mkString("")
      CampaignCache(Json.parse(body).as[List[Campaign]])
    }
  }
}
