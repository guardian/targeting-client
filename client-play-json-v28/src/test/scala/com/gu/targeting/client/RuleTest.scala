package com.gu.targeting.client

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class RuleTest extends AnyFreeSpec with Matchers {
  val tagsToMatch = List("should_match_1", "should_match_2")
  val tagsToExclude = List("should_exclude_1", "should_exclude_2")

  val rule = Rule(tagsToMatch, tagsToExclude, false)

  "A rule with required and excluded tags" - {
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

  val lackingOnlyRule = Rule(List.empty, tagsToExclude, false)

  "A rule with only lacking tags" - {
    "will never match anything" in {
      assert(!Rule.evaluate(lackingOnlyRule, tagsToMatch))
      assert(!Rule.evaluate(lackingOnlyRule, List.empty))
    }
  }

  val matchAllRule = Rule(List.empty, tagsToExclude, true)

  "A rule with matchAllTags set" - {
    "will match things with arbitrary tags" in {
      assert(Rule.evaluate(matchAllRule, tagsToMatch))
    }

    "will match things with no tags" in {
      assert(Rule.evaluate(matchAllRule, List.empty))
    }

    "won’t match things with its excluded tags" in {
      assert(!Rule.evaluate(matchAllRule, tagsToMatch ++ tagsToExclude))
    }
  }
}
