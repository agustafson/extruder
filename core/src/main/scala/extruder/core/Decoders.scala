package extruder.core

trait Decoders { self: DecodeTypes =>
  protected def mkDecoder[F[_], T](f: (List[String], Option[T], DecodeData) => F[T]): Dec[F, T]

  protected def selectOption[F[_], A](path: List[String], primary: Option[A], secondary: Option[A])(
    implicit F: Eff[F],
    hints: Hint
  ): F[A] =
    (primary, secondary) match {
      case (None, None) => F.missing(s"Could not find value at '${hints.pathToString(path)}' and no default available")
      case (None, Some(value)) => F.pure(value)
      case (Some(value), _) => F.pure(value)
    }

  def apply[F[_], T](implicit decoder: Dec[F, T]): Dec[F, T] = decoder
}

trait Decode { self: DecodeTypes =>
  protected def prepareInput[F[_]](namespace: List[String], input: InputData)(
    implicit F: Eff[F],
    hints: Hint
  ): F[DecodeData]

  /** Decode the specified type from given data source
    * If the data source is asynchronous by nature this method will wait for the duration specified in `hints` for decoding to timeout
    *
    * @param input   data source
    * @param decoder implicit [[Decoder]] instance
    * @param F       implicit [[Eff]] instance
    * @param hints   implicit [[Hints]] instance
    * @tparam A type to be decoded
    * @return Either the requested type or a non-empty list of validation errors
    */
  def decode[A](
    input: InputData
  )(implicit decoder: Dec[Validation, A], F: Eff[Validation], hints: Hint): Validation[A] =
    decode[Validation, A](input)

  /** Decode the specified type from given data source in a given namespace
    * If the data source is asynchronous by nature this method will wait for the duration specified in `hints` for decoding to timeout
    *
    * @param namespace namespace within the data source to look for values
    * @param input     data source
    * @param decoder   implicit [[Decoder]] instance
    * @param F         implicit [[Eff]] instance
    * @param hints     implicit [[Hints]] instance
    * @tparam A type to be decoded
    * @return Either the requested type or a non-empty list of validation errors
    */
  def decode[A](
    namespace: List[String],
    input: InputData
  )(implicit decoder: Dec[Validation, A], F: Eff[Validation], hints: Hint): Validation[A] =
    decode[Validation, A](namespace, input)

  /** Decode the specified type from given data source, wrapping the result in specified target monad
    * If the data source is asynchronous by nature this method will wait for the duration specified in `hints` for decoding to timeout
    *
    * @param input   data source
    * @param decoder implicit [[Decoder]] instance
    * @param F       implicit [[Eff]] instance
    * @param hints   implicit [[Hints]] instance
    * @tparam F target monad (e.g. [[scala.util.Try]])
    * @tparam A type to be decoded
    * @return The requested type wrapped in the target monad
    */
  def decode[F[_], A](input: InputData)(implicit decoder: Dec[F, A], F: Eff[F], hints: Hint): F[A] =
    decode[F, A](List.empty, input)

  /** Decode the specified type from given data source in a given namespace, wrapping the result in specified target monad
    * If the data source is asynchronous by nature this method will wait for the duration specified in `hints` for decoding to timeout
    * @param namespace namespace within the data source to look for values
    * @param input data source
    * @param decoder implicit [[Decoder]] instance
    * @param F implicit [[Eff]] instance
    * @param hints implicit [[Hints]] instance
    * @tparam F target monad (e.g. [[scala.util.Try]])
    * @tparam A type to be decoded
    * @return The requested type wrapped in the target monad
    */
  def decode[F[_], A](
    namespace: List[String],
    input: InputData
  )(implicit decoder: Dec[F, A], F: Eff[F], hints: Hint): F[A] =
    F.flatMap(prepareInput(namespace, input))(decoder.read(namespace, None, _))

  /** Create a table of parameters as they should appear in the data source
    *
    * @param params implicit [[Parameters]] type class as a representation of `T`
    * @param hints implicit [[Hints]] instance for formatting parameter paths
    * @tparam A type to be represented
    * @return a String table of parameters
    */
  def parameters[A](implicit params: Parameters[A], hints: Hint): String =
    parameters(List.empty[String])

  /** Create a table of parameters as they should appear in the data source in a given namespace
    *
    * @param namespace namespace within the data source
    * @param params implicit [[Parameters]] type class as a representation of `T`
    * @param hints implicit [[Hints]] instance for formatting parameter paths
    * @tparam A type to be represented
    * @return a String table of parameters
    */
  def parameters[A](namespace: List[String])(implicit params: Parameters[A], hints: Hint): String =
    FormatParameters.asTable[A](hints.pathToString, namespace)
}

trait DecodeFromDefaultSource { self: Decode with DecodeTypes =>
  protected def loadInput[F[_]](implicit F: Eff[F]): F[InputData]

  /** Decode the specified type from a default data source
    * If the data source is asynchronous by nature this method will wait for the duration specified in `hints` for decoding to timeout
    *
    * @param decoder implicit [[Decoder]] instance
    * @param F       implicit [[Eff]] instance
    * @param hints   implicit [[Hints]] instance
    * @tparam A type to be decoded
    * @return Either the requested type or a non-empty list of validation errors
    */
  def decode[A](implicit decoder: Dec[Validation, A], F: Eff[Validation], hints: Hint): Validation[A] =
    decode[Validation, A]

  /** Decode the specified type from a default data source in a given namespace
    * If the data source is asynchronous by nature this method will wait for the duration specified in `hints` for decoding to timeout
    *
    * @param namespace namespace within the data source to look for values
    * @param decoder   implicit [[Decoder]] instance
    * @param F         implicit [[Eff]] instance
    * @param hints     implicit [[Hints]] instance
    * @tparam A type to be decoded
    * @return Either the requested type or a non-empty list of validation errors
    */
  def decode[A](
    namespace: List[String]
  )(implicit decoder: Dec[Validation, A], F: Eff[Validation], hints: Hint): Validation[A] =
    decode[Validation, A](namespace)

  /** Decode the specified type from a default data source, wrapping the result in specified target monad
    * If the data source is asynchronous by nature this method will wait for the duration specified in `hints` for decoding to timeout
    *
    * @param decoder implicit [[Decoder]] instance
    * @param F       implicit [[Eff]] instance
    * @param hints   implicit [[Hints]] instance
    * @tparam F target monad (e.g. [[scala.util.Try]])
    * @tparam A type to be decoded
    * @return The requested type wrapped in the target monad
    */
  def decode[F[_], A](implicit decoder: Dec[F, A], F: Eff[F], hints: Hint): F[A] =
    decode[F, A](List.empty)

  /** Decode the specified type from a default data source in a given namespace, wrapping the result in specified target monad
    * If the data source is asynchronous by nature this method will wait for the duration specified in `hints` for decoding to timeout
    *
    * @param namespace namespace within the data source to look for values
    * @param decoder implicit [[Decoder]] instance
    * @param F         implicit [[Eff]] instance
    * @param hints implicit [[Hints]] instance
    * @tparam F target monad (e.g. [[scala.util.Try]])
    * @tparam A type to be decoded
    * @return The requested type wrapped in the target monad
    */
  def decode[F[_], A](namespace: List[String])(implicit decoder: Dec[F, A], F: Eff[F], hints: Hint): F[A] =
    F.flatMap(loadInput)(decode[F, A](namespace, _))
}

trait Decoder[F[_], T, C] {
  def read(path: List[String], default: Option[T], input: C): F[T]
}

trait DecoderRefute[T]

trait DecodeTypes extends DataSource {
  type DecodeData
  type Dec[F[_], T] <: Decoder[F, T, DecodeData]
  type DecRefute[T] <: DecoderRefute[T]
}
