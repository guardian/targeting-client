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

publishTo := Some(
  if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
  else Opts.resolver.sonatypeReleases
)

licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

scmInfo := Some(ScmInfo(
  url("https://github.com/guardian/targeting-client"),
  "scm:git:git@github.com:guardian/targeting-client.git"
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
