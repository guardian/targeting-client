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

pomExtra := (
  <url>https://github.com/guardian/targeting-client</url>
    <scm>
      <connection>scm:git@github.com:guardian/targeting-client.git</connection>
      <developerConnection>scm:git@github.com:guardian/targeting-client.git</developerConnection>
      <url>git@github.com:guardian/targeting-client.git</url>
    </scm>
    <developers>
      <developer>
        <id>Guardian Digital Department</id>
        <name>Guardian Digital Department</name>
        <url>https://github.com/guardian</url>
      </developer>
    </developers>
  )