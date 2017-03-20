package com.redbubble.util.json

import java.net.{URI, URL}

import cats.syntax.either._
import io.circe.{Decoder, DecodingFailure}

import scala.collection.generic.CanBuildFrom

trait DecoderOps {
  final val uriDecoder: Decoder[URI] = decodeUnsafe[String, URI](s => new URI(s))
  final val urlDecoder: Decoder[URL] = decodeUnsafe[String, URL](s => new URL(s))

  final val urlSeqDecoder: Decoder[Seq[URL]] = Decoder.instance { c =>
    val decoder = failureTolerantContainerDecoder(urlDecoder, Seq.canBuildFrom[URL])
    c.as[Seq[URL]](decoder)
  }

  final def decodeUnsafe[A: Decoder, B](unsafeF: A => B): Decoder[B] = decodeFocusEither[A, B](a => Either.catchNonFatal(unsafeF(a)))

  final def decodeFocusEither[A: Decoder, B](f: A => Either[Throwable, B]): Decoder[B] = Decoder.instance { c =>
    for {
      a <- c.as[A]
      b <- f(a).leftMap(e => DecodingFailure(e.getMessage, Nil))
    } yield b
  }

  final def decodeFocusOption[A: Decoder, B](f: A => Option[B]): Decoder[B] = Decoder.instance { c =>
    for {
      a <- c.as[A]
      b <- f(a).toRight(DecodingFailure("Option unexpectedly returned None", c.history))
    } yield b
  }

  /**
    * Decodes a container, `C`, of items of type `A`.
    * "Tolerates" failure by continuing to decode after decoding errors (and not adding them to the accumulated `C`).
    *
    * Essentially `io.circe.Decoder.decodeCanBuildFrom`.
    */
  final def failureTolerantContainerDecoder[A, C[_]](
      implicit d: Decoder[A], cbf: CanBuildFrom[Nothing, A, C[A]]): Decoder[C[A]] = new FaultTolerantSeqDecoder(d, cbf)

  /**
    * Given a `Decoder` for an `A`, decodes a sequence of `As`, at the focus of the cursor.
    *
    * A concrete instance of [[decodeContainerFocus]] for `Seq`s.
    */
  final def decodeFocusSequence[A](implicit d: Decoder[A]): Decoder[Seq[A]] =
    decodeContainerFocus[A, Seq](d, Seq.canBuildFrom[A])

  /**
    * Decoder for a container type, `C`, given a method of building (`cbf`) that container from its items, of type `A`.
    */
  final def decodeContainerFocus[A, C[_]](implicit d: Decoder[A], cbf: CanBuildFrom[Nothing, A, C[A]]): Decoder[C[A]] =
    Decoder.instance(c => c.as[C[A]](failureTolerantContainerDecoder(d, cbf)))
}

object DecoderOps extends DecoderOps
