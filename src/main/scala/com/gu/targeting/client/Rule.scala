package targeting.client

import org.cvogt.play.json.Jsonx
import play.api.libs.json._
import scala.collection.mutable.{ListBuffer, StringBuilder}

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
  implicit val ruleFormatter = Jsonx.formatCaseClassUseDefaults[Rule]
}
