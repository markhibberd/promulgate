package com.ambiata.promulgate.source

object GenSource {
  def build(name: String, deps: List[String]) = {
    s"""import sbt._
       |import Keys._
       |
       |object source extends Build {
       |  type Settings = Def.Setting[_]
       |
       |  lazy val ref =
       |    Project("${name}", file("."))
       |${gendeps(deps)}
       |}
       |""".stripMargin
  }

  def gendeps(deps: List[String]) = deps.map(dep =>
    s"""      .dependsOn(RootProject(file("../${dep}"))"""
  ).mkString("\n")
}
