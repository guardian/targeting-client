package com.gu.targeting.client

import java.net.URI
import java.util.UUID

import play.api.libs.json._

// AN stands for Apple News
case class ANComponent(
  id: UUID,
  name: String,
  active: Boolean,
  regions: ANRegions,
  rules: Seq[Rule],
  data: ComponentData
)

// ANComponent type without the id field to allow parsing before creating the component and assigning the ID
case class ANComponentWithoutID(
  name: String,
  active: Boolean,
  regions: ANRegions,
  rules: Seq[Rule],
  data: ComponentData
)

object ANComponent {
  implicit val format: Format[ANComponent] = Json.format[ANComponent]
}

object ANComponentWithoutID {
  implicit val format: Format[ANComponentWithoutID] = Json.format[ANComponentWithoutID]
}

sealed trait ComponentData

object ComponentData {
  implicit val reads: Reads[ComponentData] = (JsPath \ "type")
    .read[String]
    .flatMap(_ match {
      case "podcast"           => Json.reads[Podcast].widen
      case "newsletter-signup" => Json.reads[NewsletterSignup].widen
      case t                    => Reads.failed(s"Unrecognised ComponentData type: $t")
    })
  implicit val writes: Writes[ComponentData] =
    Writes(_ match {
      case p: Podcast =>
        (Json
          .obj("type" -> JsString("podcast"))
          ++ Json.writes[Podcast].writes(p))
      case n: NewsletterSignup =>
        (Json
          .obj("type" -> JsString("newsletter-signup"))
          ++ Json.writes[NewsletterSignup].writes(n))
    })
}

case class Podcast(
  podcastLink: URI,
  orientation: PodcastOrientation
) extends ComponentData

sealed trait PodcastOrientation
object PodcastOrientation {
  implicit val reads: Reads[PodcastOrientation] = Reads.StringReads.flatMap {
    case "automatic"  => Reads.pure(Automatic)
    case "horizontal" => Reads.pure(Horizontal)
    case s            => Reads.failed(s"Unrecognised PodcastOrientation value: $s")
  }
  implicit val writes: Writes[PodcastOrientation] = Writes.StringWrites.contramap {
    case Automatic  => "automatic"
    case Horizontal => "horizontal"
  }
}

case object Automatic extends PodcastOrientation
case object Horizontal extends PodcastOrientation

object Podcast {
  implicit val format: Format[Podcast] = Json.format[Podcast]
}

case class NewsletterSignup(
  signupComponent: String,
  name: String,
  url: String,
  description: String,
  label: String,
) extends ComponentData

object NewsletterSignup {
  implicit val format: Format[NewsletterSignup] = Json.format[NewsletterSignup]
}

case class ANRegions(US: Boolean, UK: Boolean, AU: Boolean)

object ANRegions {
  implicit val format: Format[ANRegions] = Json.format[ANRegions]
}
