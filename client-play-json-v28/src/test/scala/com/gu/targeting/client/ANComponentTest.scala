package com.gu.targeting.client

import java.util.UUID

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class ANComponentTest extends AnyFreeSpec with Matchers {
  private val podcastData = Podcast(
    podcastLink = new java.net.URI("https://www.theguardian.com/podcasts/series/today-in-focus"),
    orientation = Podcast.Orientation.Horizontal
  )

  private val newsletterSignupData = NewsletterSignup(
    imageFileName = "wellActually.png",
    name = "Well Actually",
    url = "https://www.theguardian.com/well-actually-sign-up",
    description = "Wellness advice for good life",
    label = "Free weekly newsletter",
  )

  "ANComponentWithoutID should parse newsletter-signup data" in {
    val json = Json.parse("""
      {
        "name": "newsletter component",
        "active": true,
        "regions": { "US": true, "UK": true, "AU": false },
        "rules": [],
        "data": {
          "type": "newsletter-signup",
          "imageFileName": "wellActually.png",
          "name": "Well Actually",
          "url": "https://www.theguardian.com/well-actually-sign-up",
          "description": "Wellness advice for good life",
          "label": "Free weekly newsletter"
        }
      }
      """)

    json.as[ANComponentWithoutID].data should equal(newsletterSignupData)
  }

  "ANComponentWithoutID should parse podcast data" in {
    val json = Json.parse("""
      {
        "name": "podcast component",
        "active": true,
        "regions": { "US": true, "UK": false, "AU": true },
        "rules": [],
        "data": {
          "type": "podcast",
          "podcastLink": "https://www.theguardian.com/podcasts/series/today-in-focus",
          "orientation": "horizontal"
        }
      }
      """)

    json.as[ANComponentWithoutID].data should equal(podcastData)
  }

  "ANComponent should round-trip newsletter-signup data" in {
    val component = ANComponent(
      id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
      name = "newsletter component",
      active = true,
      regions = ANRegions(US = true, UK = true, AU = false),
      rules = Seq.empty,
      data = newsletterSignupData,
    )

    val json = Json.toJson(component)

    (json \ "data" \ "type").as[String] should equal("newsletter-signup")
    json.as[ANComponent] should equal(component)
  }

  "ANComponent should round-trip podcast data" in {
    val component = ANComponent(
      id = UUID.fromString("22222222-2222-2222-2222-222222222222"),
      name = "podcast component",
      active = true,
      regions = ANRegions(US = true, UK = false, AU = true),
      rules = Seq.empty,
      data = podcastData,
    )

    val json = Json.toJson(component)

    (json \ "data" \ "type").as[String] should equal("podcast")
    json.as[ANComponent] should equal(component)
  }
}
