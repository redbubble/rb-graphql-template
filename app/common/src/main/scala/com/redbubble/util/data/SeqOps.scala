package com.redbubble.util.data

import scala.collection.mutable.{ArrayBuffer => MutableArray}
import scala.util.Random

object SeqOps {
  /**
    * Takes `size` number of (semi) random elements from a sequence. For small sizes two calls to this function on the
    * same sequence may produce the same "random" sequence.
    *
    * Isomorphic to `Random.shuffle(ts).take(size)`.
    */
  def randomTake[T](ts: Seq[T], size: Int): Seq[T] = {
    if (size <= 0) {
      Seq.empty[T]
    } else if (size > ts.length) {
      ts
    } else {
      distinctIndices(size).map(i => ts(i))
    }
  }

  private def distinctIndices(size: Int): Seq[Int] = {
    val indices = MutableArray[Int]()
    while (indices.length < size) {
      indices += Random.nextInt(size)
    }
    indices
  }
}

