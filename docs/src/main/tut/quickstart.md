#### Follow the instructions below or [try it out in Scastie!](https://scastie.scala-lang.org/janstenpickle/ozr3LrFpRdyDUqXio3RGtA/1)

Add the following to your `build.sbt`:
```scala
resolvers += Resolver.bintrayRepo("janstenpickle", "maven")
libraryDependencies += "extruder" %% "extruder" % "0.7.0"

// only if you require support for Typesafe config
libraryDependencies += "extruder" %% "extruder-typesafe" % "0.7.0"

// only if you require support for refined types
libraryDependencies += "extruder" %% "extruder-refined" % "0.7.0"
```

**Rules for resolution are specified in the declaration of the case class itself:**

In `ApplicationConfig` below `default` will be set to `100`, `noDefault` will cause a validation failure to be logged and `optional` will be set to `None`, should the data source not contain a value for each parameter.

See the page on [decoding and encoding](decode_encode.html) for more information on the different types of `decode` and `encode` methods.

```tut:silent
import cats.data.EitherT
import cats.effect.IO
import extruder.core._
import extruder.core.MapSource._

case class ApplicationConfig(default: Int = 100, noDefault: String, optional: Option[Double])

val config: Map[String, String] = Map("applicationconfig.nodefault" -> "extruder")
val applicationConfig: ApplicationConfig = ApplicationConfig(noDefault = "extruder", optional = None)

// Create a type alias for EitherT monad transformer
type EitherTIO[A] = EitherT[IO, ValidationErrors, A]

// Decode from configuration into different target monads
val decoded: Either[ValidationErrors, ApplicationConfig] = decode[ApplicationConfig](config)
val decodedIO: EitherTIO[ApplicationConfig] = decode[ApplicationConfig, EitherTIO](config)

// Encode to configuration into different target monads
val encoded: Either[ValidationErrors, Map[String, String]] = encode(applicationConfig)
val encodedIO: EitherTIO[Map[String, String]] = encode[ApplicationConfig, EitherTIO](applicationConfig)
```

It is also possible to print parameters as a table, with keys formatted as they would be in the source data:

```
val params: String = parameters[ApplicationConfig]
println(params)
```
Which outputs the following:
```
+-----------------------------+----------+--------+---------+------------------+
| Key                         | Required | Type   | Default | Permitted Values |
+-----------------------------+----------+--------+---------+------------------+
| applicationconfig.default   | N        | Int    | 100     |                  |
| applicationconfig.nodefault | Y        | String |         |                  |
| applicationconfig.optional  | N        | Double |         |                  |
+-----------------------------+----------+--------+---------+------------------+
```
