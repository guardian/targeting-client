package com.gu.targeting.client

import java.util.UUID
import com.amazonaws.services.s3._
import org.cvogt.play.json.Jsonx
import play.api.libs.json._
import com.amazonaws.services.dynamodbv2.document.{Item}
import scala.collection.mutable.ListBuffer
import org.joda.time.DateTime

case class Campaign (
  id: UUID,
  rules: List[Rule],
  priority: Int,
  activeFrom: Option[Long],
  activeUntil: Option[Long],
  displayOnSensitive: Boolean,
  fields: Fields)

object Campaign {
  implicit val campaignFormatter = Jsonx.formatCaseClassUseDefaults[Campaign]

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

  def updateStoredCampaign(campaign: Campaign, client: AmazonS3Client, bucket: String) = {
    S3.put(client, bucket, BuildInfo.version + "/" + campaign.id, Json.toJson(campaign).toString)
  }
}

class CampaignCache {
  var lastUpdate: Long = 0
  var campaigns: List[Campaign] = List()

  /// Update the rules for this engine, should be called often
  def update(client: AmazonS3Client, bucket: String) = {
    val path = BuildInfo.version + "/"
    var newCampaigns: ListBuffer[Campaign] = ListBuffer()

    S3.list(client, bucket, path).foreach( key => {
      val bytes = S3.get(client, bucket, key)
      newCampaigns += Json.fromJson[Campaign](Json.parse(new String(bytes, "utf-8")))
        .getOrElse {
          throw JsonDeserializationException(s"Could not parse campaigns in ${bucket}, ${key}")
        }
    })

    campaigns = newCampaigns.toList.filter(filterInactive)
  }

  private def filterInactive(c: Campaign): Boolean = {
    val now = DateTime.now.getMillis
    c.activeFrom.map(now > _).getOrElse(true) && c.activeUntil.map(now < _).getOrElse(true)
  }

  def getCampaignsForTags(tags: Seq[String]): List[Campaign] = {
    campaigns.filter(c => c.rules.exists(r => r.evaluate(Args(tags))))
  }
}
