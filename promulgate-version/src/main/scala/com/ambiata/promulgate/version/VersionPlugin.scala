package com.ambiata.promulgate.version

import java.io.File
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import sbt._, Keys._

object VersionPlugin extends Plugin {
  object VersionKeys {
    lazy val date    = SettingKey[Date]("current build date")
    lazy val user    = SettingKey[String]("current build user")
    lazy val machine = SettingKey[String]("current build machine")
    lazy val commish = TaskKey[String]("current git short commit hash")
    lazy val commit  = TaskKey[String]("current git full commit hash")
  }

  def promulgateVersionSettings = Seq[Sett](
    VersionKeys.date in ThisBuild    :=  VersionPlugin.now,
    VersionKeys.user in ThisBuild    :=  VersionPlugin.user,
    VersionKeys.machine in ThisBuild :=  VersionPlugin.machine,
    VersionKeys.commish in ThisBuild :=  VersionPlugin.commish(baseDirectory.value)(streams.value.log),
    VersionKeys.commit in ThisBuild  :=  VersionPlugin.commit(baseDirectory.value)(streams.value.log),
    version in ThisBuild             <<= (version in ThisBuild, VersionKeys.date, VersionKeys.commish).apply((v, d, c) => s"${v}-${VersionPlugin.timestamp(d)}-${c}")
  )

  def user =
    Option(System.getProperty("user.name")).getOrElse("<unknown>")

  def machine =
    InetAddress.getLocalHost.getHostName

  def timestamp(instant: Date, format: String = "yyyyMMddHHmmss") = {
    val formatter = new SimpleDateFormat(format)
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
    formatter.format(instant)
  }

  def commish(root: File)(logger: Logger) =
    gitlog(root, "%h")(logger)

  def commit(root: File)(logger: Logger) =
    gitlog(root, "%H")(logger)

  def gitlog(root: File, format: String)(logger: Logger) = {
    val command = s"git log --pretty=format:${format} -n  1"
    try {
      Process(command, Some(root)).lines.head
    } catch {
      case e: Throwable =>
        logger.warn(
          s"""|[promulgate-plugin] The command '"+command+"' failed and promulgate cannot retrieve a suitable commit to create a BuildInfo class.
              |Check if git is installed on the command line and if you have a valid git repository.
           """.stripMargin)

        "no-commit"
    }
  }

  lazy val now =
    new Date
}
