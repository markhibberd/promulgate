[![Build Status](https://travis-ci.org/ambiata/promulgate.svg?branch=master)](https://travis-ci.org/ambiata/promulgate)
promulgate
==========

```
prom·ul·gate
Verb
Promote or make widely known.
```

An sbt plugin wrapping up the versioning and release process for sbt
projects.

Installation
============

Add the following to your `<root>/project/plugins.sbt` file
```
resolvers += Resolver.url("ambiata-oss", new URL("https://ambiata-oss.s3.amazonaws.com"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.ambiata" % "promulgate" % "0.11.0-20141014013725-80c129f")
```

Then in `<root>/project/build.scala`:
```
import com.ambiata.promulgate.project.ProjectPlugin._

object build extends Build {
  lazy val product = Project(
    id = "product"
  , base = file(".")
  , settings =
    standardSettings ++
    promulgate.library("com.acme", "product")
  )
  ...
}
```

Instead of `promulgate.library` you can also use `app` or `all`

function  | arguments                            | description
--------- | ------------------------------------ | -----------
`library` | `package name, bucket`               | distributes the jar to a "library" bucket
`app`     | `package name, bucket`               | distributes the jar and its dependencies (using `assembly`) to an "application" bucket 
`all`     | `package name, library bucket, application bucket` | distributes both a library jar and an application jar

Features
========

The `promulgate` plugin is actually a combination of 6 plugins which can all be used independently from each other

plugin          | description                                                | used in `lib` | used in `app`
--------------- | ---------------------------------------------------------- | ------------- | -------------
`assembly`      | use the `MergeStrategy.first` for the assembly instead of [`MergeStrategy.deduplicate`](https://github.com/sbt/sbt-assembly#merge-strategy) | no            | yes 
`info`          | create a  `BuildInfo.scala` class containing project metadata: version, name, commit, timestamp... | yes | no
`notify`        | settings to notify a HipChat room                                      | yes | yes
`s3`            | settings to publish artefacts to S3                                    | yes | yes
`source`        | generate a `source.scala` sbt file containing the project dependencies | yes | yes
`version`       | modify the `version` setting to add the commit SHA and a timestamp     | yes | yes




