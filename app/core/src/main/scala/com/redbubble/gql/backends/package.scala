package com.redbubble.gql

import shapeless.tag
import shapeless.tag._

trait LimitTag

trait OffsetTag

trait PageTag

trait TotalPagesTag

trait ResultsPerPageTag

trait TotalProductsTag

trait TopicTag

package object backends {
  type Limit = Int @@ LimitTag
  type Offset = Int @@ OffsetTag
  type Page = Int @@ PageTag
  type TotalPages = Int @@ TotalPagesTag
  type ResultsPerPage = Int @@ ResultsPerPageTag
  type TotalProducts = Int @@ TotalProductsTag
  type Topic = String @@ TopicTag

  def Limit(l: Int): @@[Int, LimitTag] = tag[LimitTag](l)

  def Offset(o: Int): @@[Int, OffsetTag] = tag[OffsetTag](o)

  def Page(p: Int): @@[Int, PageTag] = tag[PageTag](p)

  def TotalPages(t: Int): @@[Int, TotalPagesTag] = tag[TotalPagesTag](t)

  def ResultsPerPage(r: Int): @@[Int, ResultsPerPageTag] = tag[ResultsPerPageTag](r)

  def TotalProducts(r: Int): @@[Int, TotalProductsTag] = tag[TotalProductsTag](r)

  def Topic(t: String): @@[String, TopicTag] = tag[TopicTag](t)
}
