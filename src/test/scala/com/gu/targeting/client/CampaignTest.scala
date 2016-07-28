package targeting.client

import play.api.libs.json._
import org.scalatest.{FreeSpec, Matchers}

class CampaignTests extends FreeSpec with Matchers {
  val expectedFields = EmailFields("testName", "testTheme", "testAbout", "testDescription", "testFrequency", "testListId")

  val requiredTags = List("should/exist", "also/exists")
  val lackingTags = List("not/this", "also/not-this")
  val rules = List(Rule(requiredTags, lackingTags))

  "Campaign should convert to JSON correctly" in {
    val campaign = Campaign(rules, None, None, false, expectedFields)
    Json.toJson(campaign).toString
  }
}
