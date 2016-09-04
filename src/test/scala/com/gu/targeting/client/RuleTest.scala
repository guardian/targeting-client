package com.gu.targeting.client

import org.scalatest.{FreeSpec, Matchers}

class RuleTest extends FreeSpec with Matchers {
  val tagsToMatch = List("should_match_1", "should_match_2")
  val tagsToExclude = List("should_exclude_1", "should_exclude_2")

  val rule = Rule(tagsToMatch, tagsToExclude)

  "A rule" - {
    "when evaluated" - {
      "returns true with matching tags" in {
        assert(rule.evaluate(tagsToMatch))
      }

      "returns false with excluded tags" in {
        assert(!rule.evaluate(tagsToExclude))
      }

      "returns false with excluded and tags" in {
        assert(!rule.evaluate(tagsToExclude ++ tagsToMatch))
      }

      "returns false with any excluded tag" in {
        assert(!rule.evaluate(tagsToMatch ++ List(tagsToExclude(0))))
      }
    }
  }
}
