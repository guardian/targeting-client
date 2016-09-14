package com.gu.targeting.client

import java.util.UUID
import com.amazonaws.services.s3._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.amazonaws.services.dynamodbv2.document.{Item}
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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

  def updateStoredCampaign(campaign: Campaign, client: AmazonS3Client, bucket: String) = {
    S3.put(client, bucket, BuildInfo.version + "/" + campaign.id, Json.toJson(campaign).toString)
  }

  def getFieldType(campaign: Campaign): Option[String] = {
    campaign.fields match {
      case _: EmailFields => Some(Fields.emailType)
      case _ => None
    }
  }
}

case class CampaignCache(campaigns: List[Campaign] = List.empty) {
  def getCampaignsForTags(tags: Seq[String]): List[Campaign] = {
    campaigns.filter(c => c.rules.exists(r => r.evaluate(tags)))
  }
}

object CampaignCache {
  /// Update the rules for this engine, should be called often
  def fetch(client: AmazonS3Client, bucket: String): Future[CampaignCache] = {
    //TODO: Seperate build number from s3 path!
    val path = BuildInfo.version + "/"

    val futureCampaignList = Future{ S3.list(client, bucket, path) }

    val newCampaigns: Future[List[Campaign]] = {
      futureCampaignList.flatMap { campaigns: List[String] =>
        getCampaigns(client, bucket, campaigns)
      }
    }

    newCampaigns.map{ campaigns =>
      val activeCampaigns = campaigns.filter(filterInactive)
      CampaignCache(activeCampaigns)
    }
  }

  def getCampaigns(client: AmazonS3Client, bucket: String, keys: List[String]): Future[List[Campaign]] = {
    val futureCampaigns = keys.map { key =>
      Future {
        val bytes = S3.get(client, bucket, key)
        Json.fromJson[Campaign](Json.parse(new String(bytes, "utf-8")))
          .getOrElse {
            throw JsonDeserializationException(s"Could not parse campaigns in ${bucket}, ${key}")
          }
      }
    }
    Future.sequence(futureCampaigns)
  }

  private def filterInactive(c: Campaign): Boolean = {
    val now = DateTime.now.getMillis
    c.activeFrom.map(now > _).getOrElse(true) && c.activeUntil.map(now < _).getOrElse(true)
  }

}
