import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object SpinalBuild extends Build {
  val spinalNameBase = "spinal-"
  val spinalVersion = "0.1-SNAPSHOT"
  val spinalRoot = "."
  val spinalScalaVersion = "2.10.3"

  val spinalGlobalSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.edcanhack.spinal",
    version := spinalVersion,
    resolvers ++= Seq(
      "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
      Resolver.sonatypeRepo("snapshots"),
      Resolver.sonatypeRepo("releases")
    )
  )

  lazy val commons = Project(id = "commons",
                             base = file(s"${spinalRoot}/commons"),
                             settings = spinalGlobalSettings)
                      .settings(
                        libraryDependencies ++= SpinalDependencies.commons
                      )

  lazy val leader = Project(id = "leader",
                            base = file(s"${spinalRoot}/leader"),
                           settings = spinalGlobalSettings)
                      .aggregate(commons).dependsOn(commons)
                      .settings(
                        libraryDependencies ++= SpinalDependencies.leader
                      )

  lazy val worker = Project(id = "worker",
                            base = file(s"${spinalRoot}/worker"),
                           settings = spinalGlobalSettings)
                      .aggregate(commons).dependsOn(commons)
                      .settings(
                        libraryDependencies ++= SpinalDependencies.worker
                      )

  lazy val singleton = Project(id = "singleton",
                               base = file(s"${spinalRoot}/singleton"),
                               settings = spinalGlobalSettings ++ assemblySettings)
                        .aggregate(worker, leader).dependsOn(worker, leader)
                        .settings(
                          libraryDependencies ++= SpinalDependencies.singleton,
                          mainClass in compile  := Some("com.edcanhack.spinal.singleton.Main"),
                          mainClass in assembly := Some("com.edcanhack.spinal.singleton.Main")
                        )

  lazy val `ext-ec2-commons` = Project(id = "ext-ec2-commons",
                                       base = file(s"${spinalRoot}/extensions/ec2-commons"),
                                      settings = spinalGlobalSettings)
                                .aggregate(commons).dependsOn(commons)
                                .settings(
                                  libraryDependencies ++= SpinalDependencies.ec2Commons
                                )

  lazy val `ext-ec2-leader` = Project(id = "ext-ec2-leader",
                                      base = file(s"${spinalRoot}/extensions/ec2-leader"),
                                      settings = spinalGlobalSettings)
                               .aggregate(`ext-ec2-commons`, leader).dependsOn(`ext-ec2-commons`, leader)
                               .settings (
                                 libraryDependencies ++= SpinalDependencies.ec2Leader
                               )

  lazy val `ext-ec2-worker` = Project(id = "ext-ec2-worker",
                                      base = file(s"${spinalRoot}/extensions/ec2-worker"),
                                      settings = spinalGlobalSettings)
                                .aggregate(`ext-ec2-commons`, worker).dependsOn(`ext-ec2-commons`, worker)
                                .settings (
                                  libraryDependencies ++= SpinalDependencies.ec2Worker
                                )
}

object SpinalDependencies {
  val logback = "ch.qos.logback" % "logback-classic" % "1.1.2"

  val commons = Seq(
    "org.slf4j" % "slf4j-api" % "1.7.7",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",

    "com.typesafe.play" %% "play-json" % "2.3.2",

    "com.twitter" %% "util-eval" % "6.18.0",

    "org.rogach" %% "scallop" % "0.9.5",

    "com.typesafe.akka" %% "akka-actor" % "2.3.4",
    "com.typesafe.akka" %% "akka-slf4j" % "2.3.4"
  )
  val leader = commons ++ Seq(
    logback
  )
  val worker = commons ++ Seq(
    logback
  )
  val singleton = leader ++ worker

  val ec2Commons = commons ++ Seq(
    "com.amazonaws" % "aws-java-sdk" % "1.8.7"
  )
  val ec2Leader = ec2Commons ++ leader ++ Seq(

  )
  val ec2Worker = ec2Commons ++ worker ++ Seq(

  )
}
