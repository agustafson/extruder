package extruder.map

import cats.Applicative
import cats.syntax.applicative._
import extruder.core.{Prune, Settings, StringReader}

trait MapDecoderInstances {
  implicit def mapDecoderStringReader[F[_]: Applicative, S <: Settings]: StringReader[F, S, Map[String, String]] =
    new StringReader[F, S, Map[String, String]] {
      override def lookup(path: List[String], settings: S, data: Map[String, String]): F[Option[String]] =
        data.get(settings.pathToString(path)).pure[F]
    }

  implicit def mapDecoderPrune[F[_], S <: Settings](implicit F: Applicative[F]): Prune[F, S, Map[String, String]] =
    new Prune[F, S, Map[String, String]] {
      override def prune(
        path: List[String],
        settings: S,
        data: Map[String, String]
      ): F[Option[(List[String], Map[String, String])]] = {
        val basePath = s"${settings.pathToString(path)}."
        val pruned = data.filterKeys(_.startsWith(basePath))
        F.pure(
          if (pruned.isEmpty) None
          else Some(pruned.keys.map(_.replace(basePath, "")).toList -> pruned)
        )
      }
    }
}
