import sbt._

object Dependencies {
  val commonsIo = "org.apache.commons" % "commons-io" % "1.3.2"
  val scalatic = "org.scalactic" %% "scalactic" % "3.1.1"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % "test"
  val http = "org.apache.httpcomponents" % "httpclient" % "4.5.12"

  val playJson = "com.typesafe.play" %% "play-json" % "2.8.1"
}
