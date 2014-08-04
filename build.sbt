name := """spinal"""

version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.10.3"

resolvers in ThisBuild ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

val spinalRoot = "."

lazy val singleton = (project in file(s"${spinalRoot}/singleton")).aggregate(worker, leader, commons).dependsOn(worker, leader, commons)
lazy val worker = (project in file(s"${spinalRoot}/worker")).aggregate(commons).dependsOn(commons)
lazy val leader = (project in file(s"${spinalRoot}/leader")).aggregate(commons).dependsOn(commons)
lazy val commons = (project in file(s"${spinalRoot}/commons"))

lazy val `ext-ec2-commons` = (project in file(s"${spinalRoot}/extensions/ec2-commons")).aggregate(commons).dependsOn(commons)
lazy val `ext-ec2-worker` = (project in file(s"${spinalRoot}/extensions/ec2-worker"))
                              .aggregate(worker, `ext-ec2-commons`).dependsOn(worker, `ext-ec2-commons`)
lazy val `ext-ec2-leader` = (project in file(s"${spinalRoot}/extensions/ec2-leader"))
                              .aggregate(leader, `ext-ec2-commons`).dependsOn(leader, `ext-ec2-commons`)