package com.gu.targeting.client

import java.util.UUID
import com.amazonaws.services.s3._
import org.cvogt.play.json.Jsonx
import play.api.libs.json._
import com.amazonaws.services.dynamodbv2.document.{Item}
import scala.collection.mutable.ListBuffer

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

  def toJson(campaign: Campaign): JsValue = {
    Json.toJson[Campaign](campaign)
  }

  def fromItem(item: Item): Campaign = {
    Json.parse(item.toJSON).as[Campaign]
  }

  def toItem(campaign: Campaign): Item = {
    Item.fromJSON(Json.toJson(campaign).toString())
  }

  def updateStoredCampaign(campaign: Campaign, client: AmazonS3Client, bucket: String, path: String = "/") = {
    S3.put(client, bucket, path, Json.toJson(campaign).toString)
  }
}

object CampaignCache {
  var lastUpdate: Long = 0
  var campaigns: List[Campaign] = List()

  /// Update the rules for this engine, should be called often
  def updateRuleCache(client: AmazonS3Client, bucket: String, path: String = "/") = {
    var newCampaigns = ListBuffer()

    val bytes = S3.get(client, bucket, path)

    val newList = Json.fromJson[List[Campaign]](Json.parse(new String(bytes, "utf-8")))
      .getOrElse {
        throw JsonDeserializationException(s"Could not parse campaigns in ${bucket}, ${path}")
      }

    campaigns = newList

    // Filter ones which are inactive or outside the time range
  }

  def getCampaignsForTags(tags: Seq[String]): List[Campaign] = {
    campaigns.filter(c => c.rules.exists(r => r.evaluate(tags)))
  }
}
