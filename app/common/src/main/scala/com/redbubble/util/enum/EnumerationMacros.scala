package com.redbubble.util.enum

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * A macro to produce a Seq of all instances of a sealed trait.
  * Based on Travis Brown's work:
  * http://stackoverflow.com/questions/13671734/iteration-over-a-sealed-trait-in-scala and taken from:
  * https://github.com/d6y/enumeration-examples/blob/master/macros/src/main/scala/EnumerationMacros.scala
  */
object EnumerationMacros {
  // This needs to run after all the case objects are defined, so reference this below (in the file) where you write your
  // instances.
  def sealedInstancesOf[A]: Seq[A] = macro sealedInstancesOf_impl[A]

  def sealedInstancesOf_impl[A: c.WeakTypeTag](c: blackbox.Context): c.Expr[Seq[A]] = {
    import c.universe._

    val symbol = weakTypeOf[A].typeSymbol.asClass

    if (!symbol.isClass || !symbol.isSealed) {
      c.abort(c.enclosingPosition, "Can only enumerate values of a sealed trait or class.")
    } else {
      val children = symbol.knownDirectSubclasses.toList
      if (!children.forall(_.isModuleClass)) {
        c.abort(c.enclosingPosition, "All children must be objects.")
      } else {
        c.Expr[List[A]] {
          def sourceModuleRef(sym: Symbol) = Ident(sym.asInstanceOf[scala.reflect.internal.Symbols#Symbol].sourceModule.asInstanceOf[Symbol])

          Apply(Select(reify(List).tree, TermName("apply")), children.map(sourceModuleRef(_)))
        }
      }
    }
  }
}
