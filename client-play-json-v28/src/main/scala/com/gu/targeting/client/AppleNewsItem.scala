package com.gu.targeting.client

import java.net.URI
import java.util.UUID

import play.api.libs.json._

sealed trait AppleNewsItem

object AppleNewsItem {
  implicit val reads: Reads[AppleNewsItem] = (JsPath \ "type")
    .read[String]
    .flatMap(_ match {
      case "podcast" => Json.reads[Podcast].widen
      case t         => Reads.failed(s"Unrecognised AppleNewsItem type: $t")
    })
  implicit val writes: Writes[AppleNewsItem] =
    Writes(_ match {
      case p: Podcast =>
        (Json
          .obj("type" -> JsString("podcast"))
          ++ Json.writes[Podcast].writes(p))
    })
}

case class Podcast(
  id: UUID,
  name: String,
  active: Boolean,
  podcastLink: URI,
  regions: AppleNewsRegions,
  rules: List[Rule]
) extends AppleNewsItem

object Podcast {
  implicit val format: Format[Podcast] = Json.format[Podcast]
}

case class AppleNewsRegions(US: Boolean, UK: Boolean, AU: Boolean)

object AppleNewsRegions {
  implicit val format: Format[AppleNewsRegions] = Json.format[AppleNewsRegions]
}