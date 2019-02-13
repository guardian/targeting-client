import Dependencies._
import ReleaseTransformations._

name := "targeting-client-play26"

organization := "com.gu"

scalaVersion := "2.12.8"

scalacOptions ++= Seq("-feature", "-deprecation")

libraryDependencies ++= Seq(
  dynamodb,
  commonsIo,
  scalatic,
  scalaTest,
  http,
  playJson26
)

releaseCrossBuild := true

crossScalaVersions := Seq(scalaVersion.value, "2.11.12")

publishArtifact in Test := false

publishMavenStyle := true

publishTo := sonatypePublishTo.value

licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

scmInfo := Some(ScmInfo(
  url("https://github.com/guardian/targeting-client"),
  "scm:git:git@github.com:guardian/targeting-client.git"
))

developers := List(Developer(
  id = "Guardian Digital Department",
  name = "Guardian Digital Department",
  email = "userhelp@theguardian.com",
  url = new URL("https://github.com/guardian")
))

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)