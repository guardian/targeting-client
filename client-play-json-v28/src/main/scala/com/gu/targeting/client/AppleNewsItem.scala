package com.gu.targeting.client

import java.net.URI
import java.util.UUID

import play.api.libs.json._
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.io.Source
import scala.util.Try

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

case class AppleNewsItemCache(appleNewsItems: List[AppleNewsItem], totalAppleNewsItems: Option[Int]) {
  def getAppleNewsItemsForTags(tags: Seq[String], stripRules: Boolean = false): List[AppleNewsItem] = {
    // TODO :need to conditionalise this for podcasts properly
      appleNewsItem.filter(c => c.rules.exists(r => Rule.evaluate(r, tags))).map { c =>
        if (stripRules) c.copy(rules = Nil) else c
      }
  }
}

object AppleNewsItemCache {
  val TOTAL_ITEMS_HEADER_NAME = "Total-Apple-News-Items"

  /** Fetch a new Apple News Items cache which contains the latest items.
   * @param url should correspond to the api end point which lists items as json (https://targeting.gutools.co.uk/api/apple-news)
   * @param limit truncate the number of items to this number
   * @param ruleLimit Any items with more rules than this number will be dropped from the results
   * @param tagLimit Any items with any rules with more tags (requiredTags + lackingTags) than this number will be dropped
   * @param ec The execution context that will execute the blocking HTTP request
   */
  def fetch(url: String, limit: Int = 100, ruleLimit: Option[Int] = None, tagLimit: Option[Int] = None)(implicit ec: ExecutionContext): Future[AppleNewsItemCache] = {
    val queryUrl = url + s"?activeOnly=true&limit=$limit&types=${Fields.allTypes.mkString(",")}"

    Future {
      val response = HttpClients.createDefault().execute(new HttpGet(queryUrl))

      val status = response.getStatusLine.getStatusCode
      if (!(200 to 299).contains(status)) {
        throw TargetingServiceException(s"Failed to get Apple News item list, status code: $status")
      }

      val body = Source.fromInputStream(response.getEntity.getContent).getLines().mkString("")

      // Total number of items, used to indicate to the client how many items they've truncated
      val header = response.getFirstHeader(TOTAL_ITEMS_HEADER_NAME)
      val totalAppleNewsItems = Try(header.getValue.toInt).toOption

      val appleNewsItems = Json.parse(body).as[List[AppleNewsItem]].filter(appleNewsItem => {
        // TODO: handle types here
        // Is the number of rules less than or equal to the limit?
        ruleLimit.forall(appleNewsItem.rules.length <= _) &&
        // And all of the rules have too many required or lacking tags
        tagLimit.forall(limit => appleNewsItem.rules.forall(rule => rule.requiredTags.length + rule.lackingTags.length <= limit))
      })

      AppleNewsItemCache(appleNewsItems, totalAppleNewsItems)
    }
  }
}