package com.redbubble.util.data

import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop.forAll
import org.scalacheck.{Prop, Properties}
import org.specs2.mutable.Specification

final class SeqOpsSpec extends Specification with SpecHelper {
  "Empty sequences" >> {
    "cannot be taken" >> {
      SeqOps.randomTake(Seq(), 0) must haveSize(0)
    }
  }

  "Non-empty sequences" >> {
    "can have slices taken" >> {
      SeqOps.randomTake(Seq(1, 2, 3), Int.MinValue) must haveSize(0)
      SeqOps.randomTake(Seq(1, 2, 3), 0) must haveSize(0)
      SeqOps.randomTake(Seq(1, 2, 3), 1) must haveSize(1)
      SeqOps.randomTake(Seq(1, 2, 3), 2) must haveSize(2)
      SeqOps.randomTake(Seq(1, 2, 3), 3) must haveSize(3)
      SeqOps.randomTake(Seq(1, 2, 3), 4) must haveSize(3)
      SeqOps.randomTake(Seq(1, 2, 3), Int.MaxValue) must haveSize(3)
    }
  }

  val takingProp = new Properties("Random taking") {
    property("taking produces lists with no duplicates") = forAll { (is: Seq[Int], size: Int) =>
      // run distinct first in case the list contains duplicates (which is valid, but not what we want to test)
      val distinct = is.distinct
      booleanProp(distinct.length >= 10 && size >= 10) ==> {
        val slice = SeqOps.randomTake(distinct, size)
        slice.distinct must beEqualTo(slice)
      }
    }
  }

  s2"Random taking$takingProp"

  private def booleanProp(b: Boolean): Prop = Prop(b)
}
