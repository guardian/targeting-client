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

    val jacksonVersion = "2.20.1"
    val jacksonDatabindVersion = "2.20.1"

    val overrides: Seq[ModuleID] = Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % jacksonVersion,
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion,
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % jacksonVersion,
      "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % jacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonDatabindVersion
    )
  }

  object PlayJsonVersion {
    val V30 = PlayJsonVersion("30", "org.playframework", "3.0.6")
  }
}
