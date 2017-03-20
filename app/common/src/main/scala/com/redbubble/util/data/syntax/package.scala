package com.redbubble.util.data

package object syntax {

  implicit final class RandomSequence[T](s: Seq[T]) {
    def randomSlice(size: Int): Seq[T] = SeqOps.randomTake(s, size)
  }

}
