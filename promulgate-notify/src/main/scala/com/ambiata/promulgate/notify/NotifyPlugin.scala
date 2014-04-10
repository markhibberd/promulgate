package com.ambiata.promulgate.notify

import sbt._, Keys._

object NotifyPlugin extends Plugin {
  object NotifyKeys {
    lazy val echoversion     = TaskKey[Unit]("echo-version")
    lazy val token           = SettingKey[String]("hipchat-token")
    lazy val room            = SettingKey[String]("hipchat-room")
  }

  import NotifyKeys._

  def notifySettings = Seq[Sett](
    echoversion <<= (name, token, room, version in ThisBuild).map(HipChat.version)
  )
}
