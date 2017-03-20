package com.redbubble.util.http

import com.redbubble.util.http.EmptyInstances.{optionEmpty, seqEmpty}
import com.redbubble.util.http.Errors.downstreamError
import com.redbubble.util.http.ServiceInteraction.interaction
import com.redbubble.util.spec.SpecHelper
import com.twitter.finagle.http.Response
import com.twitter.finagle.http.Status._
import com.twitter.util.{Await, Future}
import org.specs2.mutable.Specification

final class BackendOpsSpec extends Specification with SpecHelper with BackendOps {
  private val url = sample(genUrl)
  private val baseInteraction = interaction(url, Seq.empty, None, None)
  private val interaction404 = baseInteraction.copy(response = Some(HttpResponse(Response(NotFound))))
  private val interaction500 = baseInteraction.copy(response = Some(HttpResponse(Response(InternalServerError))))

  private val requestSequence200 = Future.value(Right(Seq(1)))
  private val requestOption200 = Future.value(Right(Some(1)))
  private val request404 = Future.value(Left(downstreamError(new Exception("bzzt"), interaction404)))
  private val request500 = Future.value(Left(downstreamError(new Exception("bzzt"), interaction500)))

  "Responses without errors" >> {
    "Sequences" >> {
      "are passed through" >> {
        Await.result(handle404AsEmpty(requestSequence200)(seqEmpty)) must beRight(Seq(1))
      }
    }
    "Options" >> {
      "are passed through" >> {
        Await.result(handle404AsEmpty[Option[Int]](requestOption200)(optionEmpty)) must beRight(Some(1))
      }
    }
  }

  "Responses with non-404 downstream errors" >> {
    "Sequences" >> {
      "are passed through" >> {
        Await.result(handle404AsEmpty(request500)(seqEmpty)) must beLeft
      }
    }
    "Options" >> {
      "are passed through" >> {
        Await.result(handle404AsEmpty(request500)(optionEmpty)) must beLeft
      }
    }
  }

  "Responses with 404 downstream errors" >> {
    "Sequences" >> {
      "are turned into empty sequences" >> {
        Await.result(handle404AsEmpty(request404)(seqEmpty)) must beRight(Seq.empty)
      }
    }
    "Options" >> {
      "are turned into None" >> {
        Await.result(handle404AsEmpty(request404)(optionEmpty)) must beRight(None)
      }
    }
  }
}
