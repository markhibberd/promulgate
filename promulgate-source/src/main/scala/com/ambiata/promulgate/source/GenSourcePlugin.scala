package com.ambiata.promulgate.source

import sbt._, Keys._, complete.DefaultParsers._

object GenSourcePlugin extends Plugin {
  lazy val sourcedep        = InputKey[Unit]("source-dependency", "generate an sbt file with source deps")

  def promulgateSource = Seq(
    sourcedep                   :=   {
      val deps = spaceDelimited("dependency [dependency ...]").parsed.toList
      println("Generating for [" + deps.mkString(", ") + "]")
      val build = GenSource.build(name.value, deps)
      IO.write(baseDirectory.value / "project" / "source.scala", build)
      println(s"Generated ${baseDirectory.value}/project/source.scala")
      println(s"Run `sbt reload` to use this configuration")
    }
  )
}
