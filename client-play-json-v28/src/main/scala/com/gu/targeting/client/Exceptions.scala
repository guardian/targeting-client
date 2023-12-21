package com.gu.targeting.client

// Different kinds of exceptions that allow users of this library to make meaningful decisions when errors occur
// For example converting the TargetingNotFound exception into a 404.

case class JsonDeserializationException(msg: String) extends RuntimeException(msg)
case class TargetingServiceException(msg: String) extends RuntimeException(msg)