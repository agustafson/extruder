---
layout: docs
title:  "Decoding and Encoding"
position: 3
---
* TOC
{:toc}

# Decoding and Encoding Basics
There are a few types of methods for encoding and decoding documented below, please see the API documentation for [decoding](api/extruder/core/Decode.html) and [encoding](api/extruder/core/Encode.html) for more details.

Normally when decoding you are required to pass the data and an optional namespace, however some data sources may provide default data, such as from [system properties](api/extruder/system/SystemPropertiesSource.html), [environment variables](api/extruder/system/EnvironmentConfig$.html) or [typesafe config](api/extruder/typesafe/TypesafeConfigSource$.html). These all extend the trait [`DecodeFromDefaultSource`](api/extruder/core/DecodeFromDefaultSource.html) which includes a set of `decode` methods which do not require data to be passed.

## Advanced Examples
Extruder includes some default implementations for `IO`, `Future`, `Either` and `ValidatedNel`, as well as extension modules for [Monix] and [FS2].

```tut:silent
import cats.data.EitherT
import cats.effect.IO
import extruder.core._
import extruder.core.EitherThrowable
import extruder.core.MapSource._
import scala.util.Try
import cats.instances.all._

decode[String, IO](List("a"), Map("a" -> "b")) // returns IO("b")
decode[String, Try](List("a"), Map("a" -> "b")) // returns Try("b")

// Type alias for EitherT monad transformer
type EitherTIO[A] = EitherT[IO, ValidationErrors, A]
decode[String, EitherTIO](List("a"), Map("a" -> "b")) // returns EitherT[IO, ValidationErrors, String](IO(Right("b")))
```

# Implementing Your Own Target Monads

As mentioned in [Concepts](concepts.html), Extruder allows you to specify target monads for your desired return types. Type classes can be created implicitly 


{% include references.md %}
