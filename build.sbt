name := """bloks"""
organization := "exul.bloks"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies += guice

libraryDependencies ++= Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
	"com.typesafe.play" %% "play-slick" % "5.0.0",
	"com.typesafe.slick" %% "slick-codegen" % "3.3.2",
    "com.typesafe.play" %% "play-json" % "2.8.1",
    "org.postgresql" % "postgresql" % "42.2.11",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
    "org.mindrot" % "jbcrypt" % "0.4",
    specs2 % Test
)

libraryDependencies += "com.typesafe.play" %% "play-mailer" % "8.0.1"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "8.0.1"
libraryDependencies += "ky.korins" %% "blake3" % "2.8.0"
