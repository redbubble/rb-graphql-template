package com.redbubble.hawk.spec

import com.redbubble.hawk.util.Time._
import com.redbubble.hawk.util.{Millis, Seconds, Time}
import org.joda.time.DateTime
import org.scalacheck.{Arbitrary, Gen}

trait Generators {
  private lazy val now = nowUtc.asDateTime

  val genTime: Gen[Time] = Gen.chooseNum(now.getMillis, now.plusYears(100).getMillis).map(ms => Time(Millis(ms)))
  val genMillis: Gen[Millis] = genTime.map(_.millis)
  val genSeconds: Gen[Seconds] = genTime.map(_.asSeconds)

  implicit def arbMillis: Arbitrary[Millis] = Arbitrary(genMillis)

  implicit def arbSeconds: Arbitrary[Seconds] = Arbitrary(genSeconds)

  implicit def arbTime: Arbitrary[Time] = Arbitrary(genTime)

  val genDateTime: Gen[DateTime] = genMillis.map(m => utcTime(m).asDateTime)

  implicit def arbDateTime: Arbitrary[DateTime] = Arbitrary(genDateTime)
}
