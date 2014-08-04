name := """spinal"""

version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.10.3"

resolvers in ThisBuild ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val singleton = (project in file("./singleton")).aggregate(worker, leader, commons).dependsOn(worker, leader, commons)
lazy val worker = (project in file("./worker")).aggregate(commons).dependsOn(commons)
lazy val leader = (project in file("./leader")).aggregate(commons).dependsOn(commons)
lazy val commons = (project in file("./commons"))
