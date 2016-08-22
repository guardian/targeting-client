package com.gu.targeting.client

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Rule(requiredTags: List[String], lackingTags: List[String]) {
  def evaluate(tags: Seq[String]): Boolean = {
    for (tag <- requiredTags) {
      if (!tags.contains(tag)) {
        return false
      }
    }

    for (tag <- lackingTags) {
      if (tags.contains(tag)) {
        return false
      }
    }

    return true
  }
}

object Rule {
  implicit val ruleFormat = (
    (JsPath \ "requiredTags").format[List[String]] and
    (JsPath \ "lackingTags").format[List[String]]
  )(Rule.apply, unlift(Rule.unapply))
}
