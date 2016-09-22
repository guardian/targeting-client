import sbt._

object Dependencies {
  val awsSdk = "com.amazonaws" % "aws-java-sdk" % "1.11.26"
  val commonsIo = "org.apache.commons" % "commons-io" % "1.3.2"
  val scalatic = "org.scalactic" %% "scalactic" % "2.2.6"
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  val http = "org.apache.httpcomponents" % "httpclient" % "4.3.4"

  val playJson25 = "com.typesafe.play" %% "play-json" % "2.5.4"
  val playJson24 = "com.typesafe.play" %% "play-json" % "2.4.8"
}
