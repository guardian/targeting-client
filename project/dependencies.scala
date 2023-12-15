import sbt._

object Dependencies {
  val commonsIo = "org.apache.commons" % "commons-io" % "1.3.2"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % Test
  val http = "org.apache.httpcomponents" % "httpclient" % "4.5.12"

  case class PlayJsonVersion(
    majorMinorVersion: String,
    groupId: String,
    exactPlayJsonVersion: String
  ) {
    val projectId = s"client-play-json-v$majorMinorVersion"

    val lib: ModuleID = groupId %% "play-json" % exactPlayJsonVersion
  }

  object PlayJsonVersion {
    val V28 = PlayJsonVersion("28", "com.typesafe.play", "2.8.2")
    val V30 = PlayJsonVersion("30", "org.playframework", "3.0.1")
  }
}
