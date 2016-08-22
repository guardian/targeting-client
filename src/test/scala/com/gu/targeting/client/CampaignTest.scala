package com.gu.targeting.client

import java.util.UUID
import play.api.libs.json._
import org.scalatest.{FreeSpec, Matchers}

class CampaignTests extends FreeSpec with Matchers {
  val expectedFields = EmailFields("testName", "testTheme", "testAbout", "testDescription", "testFrequency", "testListId")

  val requiredTags = List("should/exist", "also/exists")
  val lackingTags = List("not/this", "also/not-this")
  val rules = List(Rule(requiredTags, lackingTags))

  val id = UUID.randomUUID()

  "Campaign should convert to JSON correctly" in {
    val campaign = Campaign(id, rules, 10, None, None, false, expectedFields)
    Json.toJson(campaign).toString
  }
}
