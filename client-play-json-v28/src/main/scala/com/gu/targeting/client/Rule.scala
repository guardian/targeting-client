package com.gu.targeting.client

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Rule(requiredTags: List[String], lackingTags: List[String], matchAllTags: Boolean)

object Rule {
  implicit val ruleFormat = (
    (JsPath \ "requiredTags").format[List[String]] and
      (JsPath \ "lackingTags").format[List[String]] and
      (JsPath \ "matchAllTags").format[Boolean]
    )(Rule.apply, unlift(Rule.unapply))

  def evaluate(rule: Rule, tags: Seq[String]): Boolean = {
    if (rule.matchAllTags) tags.intersect(rule.lackingTags).isEmpty
    else tags.intersect(rule.requiredTags).nonEmpty && tags.intersect(rule.lackingTags).isEmpty
  }

}
