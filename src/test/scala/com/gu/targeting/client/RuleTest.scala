package com.gu.targeting.client

import org.scalatest.{FreeSpec, Matchers}

class RuleTest extends FreeSpec with Matchers {
  val tagsToMatch = List("should_match_1", "should_match_2")
  val tagsToExclude = List("should_exclude_1", "should_exclude_2")

  val rule = Rule(tagsToMatch, tagsToExclude, false)

  "A rule" - {
    "when evaluated" - {
      "returns true with matching tags" in {
        assert(Rule.evaluate(rule, tagsToMatch))
      }

      "returns true with any matching tags" in {
        assert(Rule.evaluate(rule, List(tagsToMatch(0))))
      }

      "returns false with excluded tags" in {
        assert(!Rule.evaluate(rule, tagsToExclude))
      }

      "returns false with excluded and tags" in {
        assert(!Rule.evaluate(rule, tagsToExclude ++ tagsToMatch))
      }

      "returns false with any excluded tag" in {
        assert(!Rule.evaluate(rule, List(tagsToMatch(0), tagsToExclude(0))))
      }

    }
  }
}
