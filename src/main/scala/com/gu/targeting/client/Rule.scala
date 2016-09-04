package com.gu.targeting.client

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Rule(requiredTags: List[String], lackingTags: List[String]) {
  def evaluate(tags: Seq[String]): Boolean = {
    tags.intersect(requiredTags).nonEmpty &&
      tags.intersect(lackingTags).isEmpty
  }
}

object Rule {
  implicit val ruleFormat = (
    (JsPath \ "requiredTags").format[List[String]] and
    (JsPath \ "lackingTags").format[List[String]]
  )(Rule.apply, unlift(Rule.unapply))
}
