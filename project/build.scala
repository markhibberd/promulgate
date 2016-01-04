import sbt._
import Keys._

import ohnosequences.sbt.SbtS3Resolver.autoImport.{s3 => ss33, _}
import com.amazonaws.services.s3.model.Region
import com.amazonaws.auth._, profile._

object build extends Build {
  type Sett = sbt.Def.Setting[_]

  lazy val standardSettings: Seq[Sett] =
    Defaults.defaultSettings ++ Seq[Sett](
      organization := "com.ambiata"
    , sbtPlugin := true
    , scalaVersion := "2.10.3"
    , scalacOptions := Seq(
        "-deprecation"
      , "-unchecked"
      , "-optimise"
      , "-Ywarn-all"
      , "-Xlint"
      , "-Xfatal-warnings"
      , "-feature"
      , "-language:_"
      )
    ) ++ Seq(
      publishMavenStyle           := false
    , publishArtifact in Test     := false
    , pomIncludeRepository        := { _ => false }
    , publishTo := Some (S3Resolver(
        new EnvironmentVariableCredentialsProvider() |
        new InstanceProfileCredentialsProvider()
      , false
      , Region.AP_Sydney
      , com.amazonaws.services.s3.model.CannedAccessControlList.BucketOwnerFullControl) ("ambiata-oss-publish", ss33("ambiata-oss")).withIvyPatterns)
    )

  lazy val promulgate = Project(
    id = "promulgate"
  , base = file(".")
  , settings = standardSettings ++ Seq[Sett](
      name := "promulgate",
      version in ThisBuild := "0.11.0"
    ) ++ VersionPlugin.uniqueVersionSettings
  , aggregate = Seq(source, info, notify_, assembly, s3, version_, project_)
  ).dependsOn(source)
   .dependsOn(info)
   .dependsOn(notify_)
   .dependsOn(assembly)
   .dependsOn(s3)
   .dependsOn(version_)
   .dependsOn(project_)

  lazy val source = Project(
    id = "source"
  , base = file("promulgate-source")
  , settings = standardSettings ++ Seq[Sett](
      name := "promulgate-source"
    )
  )

  lazy val info = Project(
    id = "info"
  , base = file("promulgate-info")
  , settings = standardSettings ++ Seq[Sett](
      name := "promulgate-info"
    )
  ).dependsOn(version_)


  lazy val assembly = Project(
    id = "assembly"
  , base = file("promulgate-assembly")
  , settings = standardSettings ++ Seq[Sett](
      name := "promulgate-assembly"
    , addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.2")
    )
  )

  lazy val s3 = Project(
    id = "s3"
  , base = file("promulgate-s3")
  , settings = standardSettings ++ Seq[Sett](
      name := "promulgate-s3"
    , resolvers += "Era7 maven releases" at "http://releases.era7.com.s3.amazonaws.com"
    // Exclude joda-time as they are resolved with wildcards
    , addSbtPlugin(("com.typesafe.sbt" % "sbt-s3" % "0.5") exclude("joda-time", "joda-time"))
    , addSbtPlugin(("ohnosequences" % "sbt-s3-resolver" % "0.13.1") exclude("joda-time", "joda-time"))
    , libraryDependencies ++= Seq("joda-time" % "joda-time" % "2.2")
    )
  ).dependsOn(assembly)

  lazy val notify_ = Project(
    id = "notify"
  , base = file("promulgate-notify")
  , settings = standardSettings ++ Seq[Sett](
      name := "promulgate-notify",
      libraryDependencies := Seq(
        "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"
      )
    )
  )

  lazy val version_ = Project(
    id = "version"
  , base = file("promulgate-version")
  , settings = standardSettings ++ Seq[Sett](
      name := "promulgate-version"
    )
  )

  lazy val project_ = Project(
    id = "project"
  , base = file("promulgate-project")
  , settings = standardSettings ++ Seq[Sett](
      name := "promulgate-project"
    )
  ).dependsOn(source)
   .dependsOn(info)
   .dependsOn(notify_)
   .dependsOn(assembly)
   .dependsOn(s3)
   .dependsOn(version_)

}
