import sbt._
import Keys._

import ohnosequences.sbt.SbtS3Resolver.{s3 => ss33, _}
import com.amazonaws.services.s3.model.Region

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
    ) ++ S3Resolver.defaults ++ Seq(
      publishMavenStyle           := false
    , publishArtifact in Test     := false
    , pomIncludeRepository        := { _ => false }
    , publishTo                   <<= (s3credentials).apply((creds) =>
      Some(S3Resolver(creds, false, Region.AP_Sydney)("ambiata-oss-publish", ss33("ambiata-oss")).withIvyPatterns))
    )

  lazy val promulgate = Project(
    id = "promulgate"
  , base = file(".")
  , settings = standardSettings ++ Seq[Sett](
      name := "promulgate",
      version in ThisBuild := "0.11.0"
    ) ++ VersionPlugin.uniqueVersionSettings
  , aggregate = Seq(source, info, notify_, assembly, s3, version_)
  ).dependsOn(source)
   .dependsOn(info)
   .dependsOn(notify_)
   .dependsOn(assembly)
   .dependsOn(s3)
   .dependsOn(version_)

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
    , addSbtPlugin("com.typesafe.sbt" % "sbt-s3" % "0.5")
    , addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.10.1")
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
}
