import Dependencies._
import ReleaseTransformations._

name := "targeting-client"

organization := "com.gu"

scalaVersion := "2.13.8"

scalacOptions ++= Seq("-feature", "-deprecation")

libraryDependencies ++= Seq(
  commonsIo,
  scalaTest,
  http,
  playJson
)

crossScalaVersions := Seq(scalaVersion.value, "2.12.15")

Test / publishArtifact := false

publishMavenStyle := true

publishTo := sonatypePublishToBundle.value

licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

scmInfo := Some(ScmInfo(
  url("https://github.com/guardian/targeting-client"),
  "scm:git:git@github.com:guardian/targeting-client.git"
))

homepage := Some(url("https://github.com/guardian/targeting-client"))

developers := List(Developer(
  id = "Guardian Digital Department",
  name = "Guardian Digital Department",
  email = "userhelp@theguardian.com",
  url = url("https://github.com/guardian")
))

releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)