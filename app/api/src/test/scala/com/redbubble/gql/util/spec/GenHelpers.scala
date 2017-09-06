package com.redbubble.gql.util.spec

import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

import cats.data.NonEmptyList
import cats.data.NonEmptyList.fromListUnsafe
import org.scalacheck.Gen

trait GenHelpers {
  final val genNonEmptyString: Gen[String] = Gen.alphaStr.suchThat(i => !i.isEmpty)

  final val genPositiveInt: Gen[Int] = Gen.posNum[Int]

  final val genMillis: Gen[Long] = {
    val now = Instant.now()
    val hundredYearsFuture = now.plus(100 * 365, DAYS)
    Gen.chooseNum(now.toEpochMilli, hundredYearsFuture.toEpochMilli)
  }

  final val genInstant: Gen[Instant] = genMillis.map(Instant.ofEpochMilli)

  // Note. This sometimes fails, so we fetch it twice to reduce that liklihood. Yeah.
  final def sample[T](gen: Gen[T]): T = gen.sample.orElse(gen.sample).get

  final def nonEmptyListOfN[A](n: Int, gen: Gen[A]): Gen[NonEmptyList[A]] =
    Gen.listOfN(n, gen).suchThat(as => as.nonEmpty).map(fromListUnsafe)

  final def seqOfMaxN[A](n: Int, gen: Gen[A]): Gen[Seq[A]] =
    Gen.oneOf(Gen.const(Seq.empty), Gen.listOfN(n, gen))

  final def seqOf[A](gen: Gen[A]): Gen[Seq[A]] = Gen.choose(1, 5).flatMap(n => seqOfMaxN(n, gen))
}
