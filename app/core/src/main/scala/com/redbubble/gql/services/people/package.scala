package com.redbubble.gql.services

import shapeless.tag
import shapeless.tag.@@

trait PersonIdTag

package object people {
  type PersonId = Int @@ PersonIdTag

  def PersonId(i: Int): Int @@ PersonIdTag = tag[PersonIdTag](i)
}


