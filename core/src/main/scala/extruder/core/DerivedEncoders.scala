package extruder.core

import shapeless._
import shapeless.labelled.FieldType

import scala.reflect.runtime.universe.TypeTag

trait DerivedEncoders { self: Encoders with EncodeTypes =>
  implicit def cnilEncoder[F[_]](implicit F: EncEff[F]): EncT[F, CNil] = mkEncoder { (_, _, _) =>
    sys.error("Impossible!")
  }

  implicit def cconsEncoder[F[_], K <: Symbol, H, T <: Coproduct](
    implicit key: Witness.Aux[K],
    headEncode: EncT[F, H],
    tailEncode: Lazy[EncT[F, T]],
    typeEncode: Lazy[EncT[F, String]],
    F: EncEff[F]
  ): EncT[F, FieldType[K, H] :+: T] =
    mkEncoder { (path, settings, value) =>
      val chooseEncoder: F[EncodeData] = value match {
        case Inl(h) => headEncode.write(path, settings, h)
        case Inr(t) => tailEncode.value.write(path, settings, t)
      }

      F.ap2(F.pure[(EncodeData, EncodeData) => EncodeData](monoid.combine))(
        typeEncode.value.write(settings.pathWithType(path), settings, key.value.name),
        chooseEncoder
      )
    }

  implicit def unionEncoder[F[_], T, O <: Coproduct](
    implicit gen: LabelledGeneric.Aux[T, O],
    underlying: Lazy[EncT[F, O]],
    F: EncEff[F],
    lp: LowPriority,
    refute: Refute[EncoderRefute[T]],
    refuteShow: Refute[Show[T]],
    refuteMultiShow: Refute[MultiShow[T]],
    neOpt: T <:!< Option[_],
    neCol: T <:!< TraversableOnce[_]
  ): EncT[F, T] =
    mkEncoder((path, settings, value) => underlying.value.write(path, settings, gen.to(value)))

  trait DerivedEncoder[T, F[_], Repr <: HList] {
    def write(path: List[String], settings: Sett, value: Repr): F[EncodeData]
  }

  implicit def hNilDerivedEncoder[T, F[_]](implicit F: EncEff[F]): DerivedEncoder[T, F, HNil] =
    new DerivedEncoder[T, F, HNil] {
      override def write(path: List[String], settings: Sett, value: HNil): F[EncodeData] = F.pure(monoid.empty)
    }

  implicit def hConsDerivedEncoder[T, F[_], K <: Symbol, V, TailRepr <: HList](
    implicit key: Witness.Aux[K],
    F: EncEff[F],
    encoder: Lazy[EncT[F, V]],
    tailEncoder: Lazy[DerivedEncoder[T, F, TailRepr]]
  ): DerivedEncoder[T, F, FieldType[K, V] :: TailRepr] =
    new DerivedEncoder[T, F, FieldType[K, V] :: TailRepr] {
      override def write(path: List[String], settings: Sett, value: FieldType[K, V] :: TailRepr): F[EncodeData] = {
        val fieldName = key.value.name

        F.ap2(F.pure[(EncodeData, EncodeData) => EncodeData](monoid.combine))(
          encoder.value.write(path :+ fieldName, settings, value.head),
          tailEncoder.value.write(path, settings, value.tail)
        )
      }
    }

  implicit def productEncoder[F[_], T, GenRepr <: HList](
    implicit gen: LabelledGeneric.Aux[T, GenRepr],
    tag: TypeTag[T],
    F: EncEff[F],
    encoder: Lazy[DerivedEncoder[T, F, GenRepr]],
    lp: LowPriority,
    refute: Refute[EncRefute[T]],
    refuteShow: Refute[Show[T]],
    refuteMultiShow: Refute[MultiShow[T]]
  ): EncT[F, T] = {
    lazy val className: String = tag.tpe.typeSymbol.name.toString
    mkEncoder[F, T] { (path, settings, value) =>
      val newPath =
        if (settings.includeClassNameInPath) path :+ className
        else path
      encoder.value.write(newPath, settings, gen.to(value))
    }
  }
}
