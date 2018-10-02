name := "oneandone.oneandone-cloudserver-sdk-scala"

version := "0.1"

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.10.6")


licenses := Seq("Apache License, Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))

libraryDependencies ++= Seq(
  "com.softwaremill.sttp" %% "core" % "1.3.3",
  "com.softwaremill.sttp" %% "akka-http-backend" % "1.3.3",
  "com.softwaremill.sttp" %% "json4s" % "1.3.3",
  "org.json4s" %% "json4s-jackson" % "3.6.1",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "com.typesafe" % "config" % "1.3.2" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.9.0" % "test",
  "org.apache.logging.log4j" % "log4j-api" % "2.9.0" % "test",
  "org.apache.logging.log4j" % "log4j-core" % "2.9.0" % "test"
)

pomExtra := (
  <developers>
    <developer>
      <name>Ali Bazlamit</name>
      <id>alibazlamit</id>
    </developer>
    <developer>
      <name>ali</name>
      <id>alibazlamit</id>
    </developer>
  </developers>
  )