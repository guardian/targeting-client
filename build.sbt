import Dependencies._

scalaVersion in ThisBuild := "2.11.8"

description in ThisBuild := "Handles the creation and application of campaigns and their rules for The Guardians targeting system"

scalacOptions ++= Seq("-feature", "-deprecation")

val bintraySettings = Seq(
  publishMavenStyle := true,
  bintrayOrganization := Some("guardian"),
  bintrayRepository := "editorial-tools",
  licenses += ("Apache-2.0", url("https://github.com/guardian/tags-thrift-schema/blob/master/LICENSE"))
  )

lazy val targetingClientPlay24 = project.in(file("targeting-client-play24"))
  .enablePlugins(BuildInfoPlugin)
  .settings(bintraySettings: _*)
  .settings(
    name := "targeting-client-play24",
    organization := "com.gu",
    publishArtifact := true,
    sourceDirectory := baseDirectory.value / "../src",

    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "com.gu.targeting.client",

    libraryDependencies ++= Seq(
      awsSdk,
      commonsIo,
      scalatic,
      scalaTest,
      http,
      playJson24,
      playWs24
    )
  )

lazy val targetingClient = project.in(file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(bintraySettings: _*)
  .settings(
    name := "targeting-client",
    organization := "com.gu",
    publishArtifact := true,

    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "com.gu.targeting.client",

    libraryDependencies ++= Seq(
      awsSdk,
      commonsIo,
      scalatic,
      scalaTest,
      http,
      playJson25,
      playWs25
    )
  )

