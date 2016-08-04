package com.gu.targeting.client

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import org.cvogt.play.json.Jsonx
import scala.collection.mutable.ListBuffer

trait Rule {
  def evaluate(args: Args): Boolean
  def isValid(): Boolean = false
}


// Should contain anything we're going to apply rules to
case class Args(tags: Seq[String]) {

}

// Built in functions
case class HasTag(tagName: String) extends Rule {
  override def evaluate(args: Args): Boolean = {
    args.tags.contains(tagName)
  }

  override def isValid(): Boolean = {
    true // Could look up to see if the tag still exists
  }
}

// Boolean Logic
case class Not(rule: Rule) extends Rule {
  override def evaluate(args: Args): Boolean = {
    !rule.evaluate(args)
  }

  override def isValid(): Boolean = {
    rule.isValid
  }
}

case class Or(left: Rule, right: Rule) extends Rule {
  override def evaluate(args: Args): Boolean = {
    left.evaluate(args) || right.evaluate(args)
  }

  override def isValid(): Boolean = {
    left.isValid || right.isValid
  }
}

case class And(left: Rule, right: Rule) extends Rule {
  override def evaluate(args: Args): Boolean = {
    left.evaluate(args) && right.evaluate(args)
  }

  override def isValid(): Boolean = {
    left.isValid && right.isValid
  }
}

// serde
object Rule {
  val opField = "__op"

  val ruleWrites = new Writes[Rule] {
    override def writes(rule: Rule): JsValue = {
      rule match {
        case r: HasTag => hasTagFormat.writes(r).asInstanceOf[JsObject] + (opField, JsString("hasTag"))
        case r: Not => notFormat.writes(r).asInstanceOf[JsObject] + (opField, JsString("not"))
        case r: Or => orFormat.writes(r).asInstanceOf[JsObject] + (opField, JsString("or"))
        case r: And => andFormat.writes(r).asInstanceOf[JsObject] + (opField, JsString("and"))
        case other => {
          throw new UnsupportedOperationException(s"Unable to serialize rule of type ${other.getClass}")
        }
      }
    }
  }

  val ruleReads = new Reads[Rule] {
    override def reads(json: JsValue): JsResult[Rule] = {
      (json \ opField).get match {
        case JsString("hasTag") => hasTagFormat.reads(json)
        case JsString("not") => notFormat.reads(json)
        case JsString("or") => orFormat.reads(json)
        case JsString("and") => andFormat.reads(json)
        case other => JsError(s"Unexpected rule type value: ${other}")
      }
    }
  }

  implicit val ruleFormat = Format(ruleReads, ruleWrites)

  implicit val hasTagFormat: Format[HasTag] =
  (__ \ "tag").format[String].inmap(tag => HasTag(tag), (hasTag: HasTag) => hasTag.tagName)

  implicit val notFormat: Format[Not] =
  (__ \ "rule").format[Rule].inmap(rule => Not(rule), (not: Not) => not.rule)

  implicit val orFormat: Format[Or] = (
      (JsPath \ "left").format[Rule] and
      (JsPath \ "right").format[Rule]
    )(Or.apply, unlift(Or.unapply))

  implicit val andFormat: Format[And] = (
      (JsPath \ "left").format[Rule] and
      (JsPath \ "right").format[Rule]
    )(And.apply, unlift(And.unapply))
}
