package extruder.aws

import cats.Monad
import cats.data.{OptionT, ValidatedNel}
import cats.syntax.either._
import com.amazonaws.auth.{AWSCredentials, BasicAWSCredentials}
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.Size
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.string.MatchesRegex
import extruder.core.{MultiParser, MultiShow, Parser}
import extruder.refined.RefinedInstances

trait AwsCredentialsInstances extends RefinedInstances {
  import AwsCredentialsInstances._

  implicit def credentialsParser(
    implicit idParser: Parser[AwsId],
    secretParser: Parser[AwsSecret]
  ): MultiParser[AWSCredentials] =
    new MultiParser[AWSCredentials] {
      override def parse[F[_]: Monad](
        lookup: List[String] => OptionT[F, String]
      ): OptionT[F, ValidatedNel[String, AWSCredentials]] =
        for {
          id <- lookup(List(AwsId))
          secret <- lookup(List(AwsSecret))
        } yield
          idParser
            .parse(id)
            .toValidatedNel[String]
            .product(
              secretParser
                .parse(secret)
                .toValidatedNel[String]
            )
            .map { case (i, s) => new BasicAWSCredentials(i.value, s.value) }
    }

  implicit def credentialsShow: MultiShow[AWSCredentials] = new MultiShow[AWSCredentials] {
    override def show(v: AWSCredentials): Map[List[String], String] =
      Map(List(AwsId) -> v.getAWSAccessKeyId, List(AwsSecret) -> v.getAWSSecretKey)
  }
}

object AwsCredentialsInstances {
  type _20 = W.`20`.T
  type _40 = W.`40`.T

  type SizeIs[N] = Size[Interval.Closed[N, N]]
  type IdRegex = MatchesRegex[W.`"^[A-Z0-9]+$"`.T]

  type AwsId = String Refined And[SizeIs[_20], IdRegex]
  type AwsSecret = String Refined SizeIs[_40]

  val AwsId = "AwsAccessKeyId"
  val AwsSecret = "AwsSecretAccessKey"
}
