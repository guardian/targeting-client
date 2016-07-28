name := "targeting-client"
organization := "com.gu"
scalaVersion := "2.11.7"

description := "Handles the creation and application of campaigns and their rules for The Guardians targeting system"

scalacOptions ++= Seq("-feature", "-deprecation")

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.21",
  "com.typesafe.play" %% "play-json" % "2.5.4",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.cvogt" %% "play-json-extensions" % "0.6.0",
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

publishMavenStyle := true
bintrayOrganization := Some("guardian")
bintrayRepository := "editorial-tools"
licenses += ("Apache-2.0", url("https://github.com/guardian/tags-thrift-schema/blob/master/LICENSE"))
