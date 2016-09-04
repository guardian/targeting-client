package com.gu.targeting.client

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Rule(requiredTags: List[String], lackingTags: List[String])

object Rule {
  implicit val ruleFormat = (
    (JsPath \ "requiredTags").format[List[String]] and
    (JsPath \ "lackingTags").format[List[String]]
  )(Rule.apply, unlift(Rule.unapply))

  def evaluate(rule: Rule, tags: Seq[String]): Boolean = {
    tags.intersect(rule.requiredTags).nonEmpty &&
      tags.intersect(rule.lackingTags).isEmpty
  }

}
