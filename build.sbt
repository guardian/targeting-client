import Dependencies._
import ReleaseTransformations._

// TODO change to 2.12 once support for Play 2.5 is dropped
scalaVersion in ThisBuild := "2.11.8"

description in ThisBuild := "Handles the creation and application of campaigns and their rules for The Guardians targeting system"

scalacOptions ++= Seq("-feature", "-deprecation")

Sonatype.sonatypeSettings

val publishSettings = Seq(
  publishArtifact := true,
  publishMavenStyle := true,
//  publishTo := Some(
//    if (isSnapshot.value)
//      Opts.resolver.sonatypeSnapshots
//    else
//      Opts.resolver.sonatypeStaging
//  ),
  licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/guardian/targeting-client"),
    "scm:git:git@github.com:guardian/targeting-client.git"
  )),
  developers := List(
    Developer(id = "nicl", name = "Nicolas Long", email = "nicolas.long@theguardian.com", url = url("https://github.com/nicl"))
  ),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll"),
    pushChanges
  )
)

lazy val root = (project in file("."))
  .aggregate(targetingClientPlay25, targetingClientPlay26)
  .settings(publishSettings)
  .settings(publishArtifact := false)
  .settings(skip in publish := true)
  .settings(skip in compile := true)

lazy val targetingClientPlay25 = project.in(file("targeting-client-play25"))
  .settings(
    name := "targeting-client-play25",
    organization := "com.gu",
    sourceDirectory := baseDirectory.value / "../src",

    libraryDependencies ++= Seq(
      awsSdk,
      commonsIo,
      scalatic,
      scalaTest,
      http,
      playJson25
    ),

    // Note, we cannot cross-compile to 2.12 as Play 2.5 libs aren't available
    // for 2.12 unfortunately.

    publishSettings
  )

lazy val targetingClientPlay26 = project.in(file("targeting-client-play26"))
  .settings(
    name := "targeting-client-play26",
    organization := "com.gu",
    sourceDirectory := baseDirectory.value / "../src",

    libraryDependencies ++= Seq(
      awsSdk,
      commonsIo,
      scalatic,
      scalaTest,
      http,
      playJson26
    ),

    crossScalaVersions := Seq("2.11.0", "2.12.0"),

    publishSettings
  )

