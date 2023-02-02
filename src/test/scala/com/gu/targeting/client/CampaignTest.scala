package com.gu.targeting.client

import java.util.UUID

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._

class CampaignTests extends AnyFreeSpec with Matchers {
  val requiredTags = List("should/exist", "also/exists")
  val lackingTags = List("not/this", "also/not-this")
  val rules = List(Rule(requiredTags, lackingTags, false))

  val id = UUID.randomUUID()

  "Campaign should convert email campaigns to JSON correctly" in {
    val emailFields = EmailFields("testName", "testTheme", "testAbout", "testDescription", "testFrequency", "testListId")
    val campaign = Campaign(id, "name", rules, 10, None, None, false, emailFields)
    Campaign.fromJson(Json.toJson(campaign)) should equal(campaign)
  }

  "Campaign should convert participation campaigns to JSON correctly" in {
    val contactFields = Some(Seq(
      Contact("Whatsapp", "1245", "https://wa.me/", Some("https://www.theguardian.com/info/2015/aug/12/whatsapp-sharing-stories-with-the-guardian")),
      Contact("Signal", "0000", "https://signal.me/#p/", None)))
    val fields = ParticipationFields("testCallout", 1245, "test-callout-tag", Some("testDescription"), JsArray(Seq(JsString("one"), JsBoolean(false), JsString("three"))), Some("https://some.url/withAPath"), contactFields)
    val campaign = Campaign(id, "name", rules, 10, None, None, false, fields)
    Campaign.fromJson(Json.toJson(campaign)) should equal(campaign)
  }

  "Campaign should convert survey campaigns to JSON correctly" in {
    val surveyFields = SurveyFields("testName", Seq(
      SurveyQuestion("testQuestion1", true),
      SurveyQuestion("testQuestion2", false)
    ))
    val campaign = Campaign(id, "name", rules, 10, None, None, false, surveyFields)
    Campaign.fromJson(Json.toJson(campaign)) should equal(campaign)
  }

  "Campaign should convert badge campaigns to JSON correctly" in {
    val badgeFields = BadgeFields("testTag", "badgeUrl", None)
    val campaign = Campaign(id, "name", rules, 10, None, None, false, badgeFields)
    Campaign.fromJson(Json.toJson(campaign)) should equal(campaign)
  }

  ".getFieldType" - {
    "should return a campaigns type" in {
      val emailFields = EmailFields("testName", "testTheme", "testAbout", "testDescription", "testFrequency", "testListId")
      val campaign = Campaign(id, "name", rules, 10, None, None, false, emailFields)
      Campaign.getFieldType(campaign) should equal(Some(Fields.emailType))
    }
  }
}
