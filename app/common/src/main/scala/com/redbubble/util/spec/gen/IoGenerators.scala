package com.redbubble.util.spec.gen

import com.redbubble.util.io.BufOps
import com.redbubble.util.io.Charset.DefaultCharset
import com.twitter.io.Buf
import org.scalacheck.{Arbitrary, Gen}

trait IoGenerators {
  // Generates UTF-8 strings
  val genStringBuf: Gen[Buf] = Arbitrary.arbString.arbitrary.map(s => BufOps.stringToBuf(s, DefaultCharset))

  // Generates random binary data
  def genBuf: Gen[Buf] = for {
    size <- Gen.choose(1, 100)
    bytes <- Gen.listOfN(size, Arbitrary.arbByte.arbitrary)
    begin <- Gen.choose(0, size)
    end <- Gen.choose(begin, size)
  } yield Buf.ByteArray.Owned(bytes.toArray, begin, end)

  implicit val arbBuf: Arbitrary[Buf] = Arbitrary(genBuf)
}
