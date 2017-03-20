package com.redbubble.util.json

import com.redbubble.util.spec.SpecHelper
import io.circe.syntax._
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class ExceptionEncoderSpec extends Specification with SpecHelper {
  implicit val exceptionEncoder = ExceptionEncoder.exceptionEncoder

  val exceptionEncodingProp = new Properties("Exception encoding") {
    property("without exception cause") = forAll(genExceptionNoCause) { (e: scala.Exception) =>
      e.asJson.noSpaces must beEqualTo(s"""{"message":"${e.getMessage}","type":"Exception"}""")
    }
    property("with exception cause") = forAll(genExceptionCause) { (e: scala.Exception) =>
      e.asJson.noSpaces must beEqualTo(s"""{"message":"${e.getMessage}","type":"Exception","cause":"${e.getCause.getMessage}"}""")
    }
  }

  s2"Exceptions can be encoded into JSON$exceptionEncodingProp"

  implicit val throwableEncoder = ExceptionEncoder.throwableEncoder

  val throwableEncodingProp = new Properties("Throwable encoding") {
    property("without exception cause") = forAll(genThrowableNoCause) { (t: Throwable) =>
      t.asJson.noSpaces must beEqualTo(s"""{"message":"${t.getMessage}","type":"Throwable"}""")
    }
    property("with exception cause") = forAll(genThrowableCause) { (t: Throwable) =>
      t.asJson.noSpaces must beEqualTo(s"""{"message":"${t.getMessage}","type":"Throwable","cause":"${t.getCause.getMessage}"}""")
    }
  }

  s2"Throables can be encoded into JSON$throwableEncodingProp"
}
