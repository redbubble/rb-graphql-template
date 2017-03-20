package com.redbubble.util.json

import io.circe.Decoder

package object syntax {

  implicit final class SequenceDecoderSyntax[A, C[_]](val d: Decoder[A]) extends AnyVal {
    def seqDecoder: Decoder[Seq[A]] = DecoderOps.failureTolerantContainerDecoder(d, Seq.canBuildFrom[A])
  }

}
