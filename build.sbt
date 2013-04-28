name := "search-engine"

version := "0.1"

scalaVersion := "2.10.1"

libraryDependencies ++= List("com.typesafe.slick" %% "slick" % "1.0.0-RC2", //Slick
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.166")

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.2" //Jsoup

