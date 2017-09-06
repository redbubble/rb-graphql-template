package com.redbubble.gql.services

import shapeless.tag
import shapeless.tag.@@

trait PersonIdTag

trait NameTag

trait HairColourTag

trait BirthYearTag

package object people {
  type PersonId = Int @@ PersonIdTag
  type Name = String @@ NameTag
  type HairColour = String @@ HairColourTag
  type BirthYear = String @@ BirthYearTag

  def PersonId(i: Int): Int @@ PersonIdTag = tag[PersonIdTag](i)

  def Name(n: String): String @@ NameTag = tag[NameTag](n)

  def HairColour(c: String): String @@ HairColourTag = tag[HairColourTag](c)

  def BirthYear(y: String): String @@ BirthYearTag = tag[BirthYearTag](y)
}


