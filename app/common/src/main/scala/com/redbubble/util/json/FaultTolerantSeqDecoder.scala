package com.redbubble.util.json

import cats.data.{NonEmptyList, Validated}
import io.circe.{Decoder, _}

import scala.collection.generic.CanBuildFrom

/**
  * "Tolerates" failure by continuing to decode after decoding errors (and not adding them to the accumulated `C`).
  * Copied from `io.circe.SeqDecoder`.
  */
final class FaultTolerantSeqDecoder[A, C[_]](
    decodeA: Decoder[A],
    cbf: CanBuildFrom[Nothing, A, C[A]]
) extends Decoder[C[A]] {
  //noinspection ScalaStyle
  def apply(c: HCursor): Decoder.Result[C[A]] = {
    var current = c.downArray

    if (current.succeeded) {
      val builder = cbf.apply
      var failed: DecodingFailure = null

      while (failed.eq(null) && current.succeeded) {
        decodeA(current.asInstanceOf[HCursor]) match {
          // Difference from Circe! We ignore decode failures & continue to parse
          case Left(_) => current = current.right
          case Right(a) =>
            builder += a
            current = current.right
        }
      }

      if (failed.eq(null)) {
        Right(builder.result)
      } else {
        Left(failed)
      }
    } else {
      if (c.value.isArray) {
        Right(cbf.apply.result)
      } else {
        Left(DecodingFailure("CanBuildFrom for A", c.history))
      }
    }
  }

  override def decodeAccumulating(c: HCursor): AccumulatingDecoder.Result[C[A]] = {
    var current = c.downArray

    if (current.succeeded) {
      val builder = cbf.apply
      var failed = false
      val failures = List.newBuilder[DecodingFailure]

      while (current.succeeded) {
        decodeAccumulating2(decodeA, current.asInstanceOf[HCursor]) match {
          case Validated.Invalid(es) =>
            failed = true
            failures += es.head
            failures ++= es.tail
          case Validated.Valid(a) =>
            if (!failed) {
              builder += a
            }
        }
        current = current.right
      }

      if (!failed) {
        Validated.valid(builder.result)
      } else {
        failures.result match {
          case h :: t => Validated.invalid(NonEmptyList(h, t))
          case Nil => Validated.valid(builder.result)
        }
      }
    } else {
      if (c.value.isArray) {
        Validated.valid(cbf.apply.result)
      } else {
        Validated.invalidNel(DecodingFailure("CanBuildFrom for A", c.history))
      }
    }
  }

  // This was moved from a private method in Decoder
  private def decodeAccumulating2[B](decoder: Decoder[B], c: HCursor): AccumulatingDecoder.Result[B] = decoder.apply(c) match {
    case Right(a) => Validated.valid(a)
    case Left(e) => Validated.invalidNel(e)
  }
}
