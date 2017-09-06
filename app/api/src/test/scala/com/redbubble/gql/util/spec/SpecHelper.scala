package com.redbubble.gql.util.spec

import com.redbubble.util.async.singleThreadedFuturePool
import com.redbubble.util.json.CodecOps
import com.redbubble.util.log.Logger
import org.specs2.ScalaCheck
import org.specs2.execute.{Failure, Result}
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

trait SpecLogging {
  final val log = new Logger("rb-graphql-template-test")(singleThreadedFuturePool)
}

object SpecLogging extends SpecLogging

trait SpecHelper extends TestEnvironmentSetter with SpecLogging with ScalaCheck with PeopleGenerators
    with CodecOps with BeforeAll {
  self: Specification =>
  override def beforeAll(): Unit = setEnvironment()

  final def fail(message: String, t: Throwable): Result = Failure(message, "", t.getStackTrace.toList)
}
