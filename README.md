<div style="text-align:center"><img src ="https://janstenpickle.github.io/extruder/img/extruder.svg" /></div>

# Extruder

[![Typelevel Incubator](https://img.shields.io/badge/typelevel-incubator-green.svg)](http://typelevel.org/projects) [![Build Status](https://travis-ci.org/janstenpickle/extruder.svg?branch=master)](https://travis-ci.org/janstenpickle/extruder) [![codecov](https://codecov.io/gh/janstenpickle/extruder/branch/master/graph/badge.svg)](https://codecov.io/gh/janstenpickle/extruder)

This library uses [shapeless](https://github.com/milessabin/shapeless) and [cats](https://github.com/typelevel/cats) to provide a neat syntax to instantiate Scala case classes from a data source.

[See the extruder microsite for detailed documentation.](https://janstenpickle.github.io/extruder/)

# Modules
|Module|Description|Download|
|---|---|---|
| **Extruder**|Main module, includes core functionality and basic resolvers.|[ ![Download](https://api.bintray.com/packages/janstenpickle/maven/extruder/images/download.svg) ](https://bintray.com/janstenpickle/maven/extruder/_latestVersion)|
| **Typesafe Config**|Support for resolution from [Typesafe Config](https://github.com/typesafehub/config).|[ ![Download](https://api.bintray.com/packages/janstenpickle/maven/extruder/images/download.svg) ](https://bintray.com/janstenpickle/maven/extruder-typesafe/_latestVersion)|
| **Refined**|Support for [Refined](https://github.com/fthomas/refined) types.|[ ![Download](https://api.bintray.com/packages/janstenpickle/maven/extruder/images/download.svg) ](https://bintray.com/janstenpickle/maven/extruder-refined/_latestVersion)|
| **AWS**|Support for [AWS](https://aws.amazon.com/sdk-for-java/) types.|[ ![Download](https://api.bintray.com/packages/janstenpickle/maven/extruder/images/download.svg) ](https://bintray.com/janstenpickle/maven/extruder-aws/_latestVersion)|
| **Prometheus**|Support for encoding data as [Prometheus](https://prometheus.io) metrics.|[ ![Download](https://api.bintray.com/packages/janstenpickle/maven/extruder/images/download.svg) ](https://bintray.com/janstenpickle/maven/extruder-metrics-prometheus/_latestVersion)|
| **Dropwizard**|Support for encoding data as [Dropwizard](https://metrics.dropwizard.io) metrics.|[ ![Download](https://api.bintray.com/packages/janstenpickle/maven/extruder/images/download.svg) ](https://bintray.com/janstenpickle/maven/extruder-metrics-dropwizard/_latestVersion)|
| **Spectator**|Support for encoding data as [Spectator](https://github.com/Netflix/spectator) metrics.|[ ![Download](https://api.bintray.com/packages/janstenpickle/maven/extruder/images/download.svg) ](https://bintray.com/janstenpickle/maven/extruder-metrics-spectator/_latestVersion)|

## Install with SBT
Add the following to your `build.sbt`:
```scala
resolvers += Resolver.bintrayRepo("janstenpickle", "maven")
libraryDependencies += "extruder" %% "extruder" % "0.8.3"

// only if you require support for Typesafe config
libraryDependencies += "extruder" %% "extruder-typesafe" % "0.8.3"

// only if you require support for refined types
libraryDependencies += "extruder" %% "extruder-refined" % "0.8.3"

// only if you require support for AWS config
libraryDependencies += "extruder" %% "extruder-aws" % "0.8.3"

// only if you require support for prometheus encoders
libraryDependencies += "extruder" %% "extruder-metrics-prometheus" % "0.8.3"

// only if you require support for dropwizard encoders
libraryDependencies += "extruder" %% "extruder-metrics-dropwizard" % "0.8.3"

// only if you require support for spectator encoders
libraryDependencies += "extruder" %% "extruder-metrics-spectator" % "0.8.3"
```

# Participation

This project supports the Typelevel [code of conduct](http://typelevel.org/conduct.html) and aims that its channels
(mailing list, Gitter, github, etc.) to be welcoming environments for everyone.
