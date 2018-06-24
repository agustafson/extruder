package extruder.metrics.prometheus

import cats.effect.IO
import extruder.metrics.snakeCaseTransformation
import org.scalacheck.Prop
import org.scalacheck.ScalacheckShapeless._
import org.specs2.specification.core.SpecStructure
import org.specs2.{ScalaCheck, Specification}

class PrometheusRegistrySpec extends Specification with ScalaCheck {
  import TestUtils._

  override def is: SpecStructure =
    s2"""
        Can encode namespaced values $encodeNamespaced
        Can encode an object $encodeObject
        Can encode a dimensional object $encodeDimensionalObject
      """

  case class X(a: String, b: Int)

  def encodeNamespaced: Prop = prop { (value: Int, name: String) =>
    val reg = new PrometheusRegistry().encode[IO, Int](List(name), value).unsafeRunSync()
    reg
      .getSampleValue(snakeCaseTransformation(name), Array("metric_type"), Array("gauge")) === value.toDouble
  }

  def encodeObject: Prop = prop { metrics: Metrics =>
    val reg = new PrometheusRegistry().encode[IO, Metrics](metrics).unsafeRunSync
    (reg
      .getSampleValue(snakeCaseTransformation("a"), Array("metric_type"), Array("counter")) === metrics.a.value.toDouble)
      .and(
        reg.getSampleValue(snakeCaseTransformation("b"), Array("metric_type"), Array("counter")) === metrics.b.value.toDouble
      )
      .and(
        reg.getSampleValue(snakeCaseTransformation("c"), Array("metric_type"), Array("counter")) === metrics.c.value.toDouble
      )
  }

  def encodeDimensionalObject: Prop = prop { stats: Stats =>
    val reg = new PrometheusRegistry().encode[IO, Stats](stats).unsafeRunSync()
    (reg
      .getSampleValue(snakeCaseTransformation("requests"), Array("metric_type", "metrics"), Array("counter", "a")) === stats.requests.a.value.toDouble)
      .and(
        reg
          .getSampleValue(snakeCaseTransformation("requests"), Array("metric_type", "metrics"), Array("counter", "b")) === stats.requests.b.value.toDouble
      )
      .and(
        reg
          .getSampleValue(snakeCaseTransformation("requests"), Array("metric_type", "metrics"), Array("counter", "c")) === stats.requests.c.value.toDouble
      )

  }
}
