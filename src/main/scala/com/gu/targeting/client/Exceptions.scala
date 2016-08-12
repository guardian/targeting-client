package com.gu.targeting.client

// Different kinds of exceptions that allow users of this library to make meaningful decisions when errors occur
// For example converting the TargetingNotFound exception into a 404.

case class JsonDeserializationException(msg: String) extends RuntimeException(msg)
case class TargetingNotFoundException(msg: String) extends RuntimeException(msg)
case class AuthenticationException(msg: String) extends RuntimeException(msg)
case class UnknownException(msg: String) extends RuntimeException(msg)
case class RuleParseException(msg: String) extends RuntimeException(msg)
