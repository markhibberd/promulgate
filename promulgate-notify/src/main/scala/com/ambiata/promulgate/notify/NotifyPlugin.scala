package com.ambiata.promulgate.notify

import sbt._, Keys._

object NotifyPlugin extends Plugin {
  object NotifyKeys {
    lazy val echoversion     = TaskKey[Unit]("echo-version")
    lazy val token           = SettingKey[String]("hipchat-token")
    lazy val room            = SettingKey[String]("hipchat-room")
  }

  import NotifyKeys._

  def promulgateNotifySettings =
    infer

  def promulgateNotifyHipchatSettings = Seq[Sett](
    echoversion <<= (name, token, room, version in ThisBuild).map(HipChat.version)
  )

  def infer: Seq[Sett] = (for {
    token <- Option(System.getenv("HIPCHAT_TOKEN"))
    room <- Option(System.getenv("HIPCHAT_ROOM"))
  } yield Seq(NotifyKeys.token := token, NotifyKeys.room := room, echoversion <<= (name, NotifyKeys.token, NotifyKeys.room, version in ThisBuild).map(HipChat.version))).getOrElse(Seq(echoversion <<= (name, version in ThisBuild).map(HipChat.console)))

}
