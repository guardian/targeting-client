//package targeting.client

//trait Rule {
//  def name: String
//  def evaluate(args: Args): Boolean
//  def isValid(): Boolean = false
//}
//
//
//// Should contain anything we're going to apply rules to
//case class Args(tags: Iterable[String]) {
//
//}
//
//// Built in functions
//case class HasTag(tagName: String) extends Rule {
//  def name = "hasTag"
//
//  def evaluate(args: Args): Boolean = {
//    args.tags.contains(tagName)
//  }
//
//  def isValid(): Boolean = {
//    True // Could look up to see if the tag still exists
//  }
//}
//
//// Boolean Logic
//case class Not(r: Rule) {
//  def name = "not"
//
//  def evaluate(args: Args): Boolean = {
//    !r.evaluate(args)
//  }
//
//  def isValid(): Boolean = {
//    True
//  }
//}
//
//case class Or(left: Rule, right: Rule) {
//  def name = "or"
//
//  def evaluate(args: Args): Boolean = {
//    left.evaluate(args) || right.evaluate(args)
//  }
//
//  def isValid(): Boolean = {
//    left.isValid || right.isValid
//  }
//}
//
//case class And(left: Rule, right: Rule) {
//  def name = "and"
//
//  def evaluate(args: Args): Boolean = {
//    left.evaluate(args) && right.evaluate(args)
//  }
//
//  def isValid(): Boolean = {
//    left.isValid && right.isValid
//  }
//}
//
//// serde
//object Rule {
//
//  def fromString(rule: String, offset: Int = 0): Rule = {
//    while (offset < rule.length) {
//      if (rule[i] == '(') {
//        Rule.fromString(rule, offset + 1)
//      } else if (rule[offset] == ')') {
//        // finished subrule
//      } else {
//        // process rule
//        offset += 1
//      }
//    }
//  }
//}
//
//"(or (hasTag tag/name) (not (hasTag tag/name)))"
//
//
