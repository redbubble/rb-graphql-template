package com.redbubble.gql.graphql

import com.redbubble.gql.backends._
import com.redbubble.util.spec.SpecHelper
import org.specs2.mutable.Specification

final class PaginationSpec extends Specification with SpecHelper {
  val perPage = ResultsPerPage(10)

  "Paginators at the start of their results" >> {
    val paginator = Pagination(Limit(perPage), Offset(0), Some(Offset(perPage)))

    "have more results available" >> {
      paginator.moreResultsAvailable must beTrue
    }

    "knows what page its on" >> {
      paginator.asPage(perPage) must beEqualTo(Page(1))
    }
  }

  "Paginators in the middle of a large result set" >> {
    val paginator = Pagination(Limit(perPage), Offset(perPage), Some(Offset(perPage * 2)))

    "have more results available" >> {
      paginator.moreResultsAvailable must beTrue
    }

    "knows what page its on" >> {
      paginator.asPage(perPage) must beEqualTo(Page(2))
    }
  }

  "Paginators at the end of their results" >> {
    val paginator = Pagination(Limit(perPage), Offset(perPage), None)

    "have no more results available" >> {
      paginator.moreResultsAvailable must beFalse
    }

    "knows what page its on" >> {
      paginator.asPage(perPage) must beEqualTo(Page(2))
    }
  }

  "Creation from page-based data" >> {
    val paginator = Pagination(Limit(perPage), Offset(0), None)

    "on the first and only page" >> {
      val fromPage = paginator.fromPageData(Page(1), TotalPages(1), perPage)

      "are at the end of their range" >> {
        fromPage must beEqualTo(
          paginator.copy(nextOffset = Some(Offset(0)))
        )
        fromPage.moreResultsAvailable must beFalse
      }
    }

    "in the middle of their pages" >> {
      val fromPage = paginator.fromPageData(Page(1), TotalPages(10), perPage)

      "are in the middle of their range" >> {
        fromPage must beEqualTo(
          paginator.copy(nextOffset = Some(Offset(paginator.currentOffset + perPage)))
        )
        fromPage.moreResultsAvailable must beTrue
      }
    }

    "on the last page" >> {
      val fromPage = paginator.fromPageData(Page(10), TotalPages(10), perPage)

      "are at the end of their range" >> {
        fromPage must beEqualTo(
          paginator.copy(nextOffset = Some(Offset(paginator.currentOffset)))
        )
        fromPage.moreResultsAvailable must beFalse
      }
    }


  }
}
