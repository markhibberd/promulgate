package com.ambiata.promulgate.project

import sbt._, Keys._

import com.ambiata.promulgate.s3.PromulgateS3Plugin._
import com.ambiata.promulgate.version.VersionPlugin._
import com.ambiata.promulgate.info.BuildInfoPlugin._
import com.ambiata.promulgate.notify.NotifyPlugin._
import com.ambiata.promulgate.source.GenSourcePlugin._
import com.ambiata.promulgate.assembly.AssemblyPlugin._

object ProjectPlugin extends Plugin {
  object promulgate {
    def library(pkgname: String, bucket: String): Seq[Sett] =
      promulgateVersionSettings ++
      promulgateBuildInfoSettings ++
      promulgateNotifySettings ++
      promulgateSourceSettings ++
      promulgateS3LibSettings ++ Seq(
        BuildInfoKeys.pkg := pkgname
      , S3LibKeys.bucket := bucket
      )

    def application(pkgname: String, bucket: String): Seq[Sett] =
      promulgateAssemblySettings ++
      promulgateVersionSettings ++
      promulgateBuildInfoSettings ++
      promulgateNotifySettings ++
      promulgateSourceSettings ++
      promulgateS3DistSettings ++ Seq(
        BuildInfoKeys.pkg := pkgname
      , S3DistKeys.bucket := bucket
      )

    def all(pkgname: String, lib: String, dist: String): Seq[Sett] =
      promulgateAssemblySettings ++
      promulgateVersionSettings ++
      promulgateBuildInfoSettings ++
      promulgateNotifySettings ++
      promulgateSourceSettings ++
      promulgateS3LibSettings ++
      promulgateS3DistSettings ++ Seq(
        BuildInfoKeys.pkg := pkgname
      , S3DistKeys.bucket := dist
      , S3LibKeys.bucket := lib
      )
  }
}
