import sbt._

object Dependencies {
  val awsSdkVersion = "1.11.26"
  val dynamodb = "com.amazonaws" % "aws-java-sdk-dynamodb" % awsSdkVersion

  val commonsIo = "org.apache.commons" % "commons-io" % "1.3.2"
  val scalatic = "org.scalactic" %% "scalactic" % "3.0.4"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"
  val http = "org.apache.httpcomponents" % "httpclient" % "4.3.4"

  val playJson25 = "com.typesafe.play" %% "play-json" % "2.5.18"
  val playJson26 = "com.typesafe.play" %% "play-json" % "2.6.7"
}
