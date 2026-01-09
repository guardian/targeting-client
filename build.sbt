import Dependencies._
import ReleaseTransformations._
import sbtversionpolicy.withsbtrelease.ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease

name := "targeting-client"

lazy val root = (project in file(".")).aggregate(
  client_play_json30,
).settings(
  publish / skip := true,
  sonatypeReleaseSettings
)

val sonatypeReleaseSettings = Seq(
  releaseVersion := fromAggregatedAssessedCompatibilityWithLatestRelease().value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion
  )
)

def clientWith(playJsonVersion: PlayJsonVersion) =
  Project(playJsonVersion.projectId, file(playJsonVersion.projectId))
  .settings(
    licenses := Seq(License.Apache2),
    scalaVersion := "2.13.18",
    organization := "com.gu.targeting-client",
    scalacOptions ++= Seq("-feature", "-deprecation", "-release:11"),
    libraryDependencies ++= Seq(
      commonsIo,
      scalaTest,
      http,
      playJsonVersion.lib
    ),
    dependencyOverrides ++= playJsonVersion.overrides
  )

lazy val client_play_json30 = clientWith(PlayJsonVersion.V30)
