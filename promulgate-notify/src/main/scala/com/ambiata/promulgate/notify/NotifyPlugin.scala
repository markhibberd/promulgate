package com.ambiata.promulgate.notify

import sbt._, Keys._

object NotifyPlugin extends Plugin {
  object NotifyKeys {
    lazy val echoversion     = TaskKey[Unit]("echo-version")
    lazy val token           = SettingKey[String]("hipchat-token")
    lazy val room            = SettingKey[String]("hipchat-room")
  }

  import NotifyKeys._

  def promulgateNotifySettings = Seq[Sett](
    echoversion <<= (name, token, room, version in ThisBuild).map(HipChat.version)
  ) ++ inferHipchatToken ++ inferHipchatRoom

  def inferHipchatToken: Seq[Sett] =
    Option(System.getenv("HIPCHAT_TOKEN")) match {
      case None => Seq()
      case Some(token) => Seq(NotifyKeys.token := token)
    }

  def inferHipchatRoom: Seq[Sett] =
    Option(System.getenv("HIPCHAT_ROOM")) match {
      case None => Seq()
      case Some(room) => Seq(NotifyKeys.room := room)
    }
}
