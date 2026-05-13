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

// AN stands for Apple News
case class ANComponent(
  id: UUID,
  name: String,
  active: Boolean,
  regions: ANRegions,
  rules: List[Rule],
  data: ComponentData
)

object ANComponent {
  implicit val format: Format[ANComponent] = Json.format[ANComponent]
}

sealed trait ComponentData

object ComponentData {
  implicit val reads: Reads[ComponentData] = (JsPath \ "type")
    .read[String]
    .flatMap(_ match {
      case "podcast" => Json.reads[Podcast].widen
      case t         => Reads.failed(s"Unrecognised ComponentData type: $t")
    })
  implicit val writes: Writes[ComponentData] =
    Writes(_ match {
      case p: Podcast =>
        (Json
          .obj("type" -> JsString("podcast"))
          ++ Json.writes[Podcast].writes(p))
    })
}

case class Podcast(
  podcastLink: URI,
) extends ComponentData

object Podcast {
  implicit val format: Format[Podcast] = Json.format[Podcast]
}

case class ANRegions(US: Boolean, UK: Boolean, AU: Boolean)

object ANRegions {
  implicit val format: Format[ANRegions] = Json.format[ANRegions]
}

case class ANComponentCache(appleNewsComponents: List[ANComponent], totalANComponents: Option[Int]) {
  def getANComponentsForTags(tags: Seq[String], stripRules: Boolean = false): List[ANComponent] = {
      appleNewsComponents.filter(c => c.rules.exists(r => Rule.evaluate(r, tags))).map { c =>
        if (stripRules) c.data match {
          case p: Podcast => c.copy(rules = Nil)
        } else c
      }
  }
}

object ANComponentCache {
  val TOTAL_COMPONENTS_HEADER_NAME = "Total-AN-Components"

  /** Fetch a new Apple News Components cache which contains the latest components.
   * @param url should correspond to the api end point which lists components as json (https://targeting.gutools.co.uk/api/apple-news)
   * @param limit truncate the number of components to this number
   * @param ruleLimit Any components with more rules than this number will be dropped from the results
   * @param tagLimit Any components with any rules with more tags (requiredTags + lackingTags) than this number will be dropped
   * @param ec The execution context that will execute the blocking HTTP request
   */
  def fetch(url: String, limit: Int = 100, ruleLimit: Option[Int] = None, tagLimit: Option[Int] = None)(implicit ec: ExecutionContext): Future[ANComponentCache] = {
    val queryUrl = url + s"?activeOnly=true&limit=$limit&types=${Fields.allTypes.mkString(",")}"

    Future {
      val response = HttpClients.createDefault().execute(new HttpGet(queryUrl))

      val status = response.getStatusLine.getStatusCode
      if (!(200 to 299).contains(status)) {
        throw TargetingServiceException(s"Failed to get Apple News component list, status code: $status")
      }

      val body = Source.fromInputStream(response.getEntity.getContent).getLines().mkString("")

      // Total number of components, used to indicate to the client how many components they've truncated
      val header = response.getFirstHeader(TOTAL_COMPONENTS_HEADER_NAME)
      val totalANComponents = Try(header.getValue.toInt).toOption

      val appleNewsComponents = Json.parse(body).as[List[ANComponent]].filter(appleNewsComponent => {
        // Is the number of rules less than or equal to the limit?
        ruleLimit.forall(appleNewsComponent.rules.length <= _) &&
        // And all of the rules have too many required or lacking tags
        tagLimit.forall(limit => appleNewsComponent.rules.forall(rule => rule.requiredTags.length + rule.lackingTags.length <= limit))
      })

      ANComponentCache(appleNewsComponents, totalANComponents)
    }
  }
}