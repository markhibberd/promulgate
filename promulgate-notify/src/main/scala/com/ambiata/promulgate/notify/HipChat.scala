package com.ambiata.promulgate.notify

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.control.NonFatal

object HipChat {
  import dispatch._, Defaults._

  val HipChatApi = "https://api.hipchat.com/v1/rooms/message"

  def console(name: String, version: String) =
    println(s"build version [$name:$version]")

  def runWithTimeout[A](f: Future[Either[Throwable, A]]): Either[Throwable, A] =
    try Await.result(f, 3 seconds)
    catch { case NonFatal(e) => Left(e) }

  def version(name: String, token: String, room: String, version: String) = {
    console(name, version)
    runWithTimeout(Http({
      url(s"${HipChatApi}?auth_token=${token}&format=json").secure << Map(
        "message_format" -> "text",
        "message"        -> s"build version [$name:$version]",
        "from"           -> "promulgate",
        "color"          -> "green",
        "room_id"        -> room
      )
    }).either) match {
      case Left(err) =>
        println(s"Couldn't send hipchat notification: ${err.getMessage}")
      case Right(res) if res.getStatusCode >= 400 =>
        println(s"Couldn't send hipchat notification: Error[${res.getStatusCode}]\n${res.getResponseBody}")
      case Right(res) =>
        ()
    }
  }
}
