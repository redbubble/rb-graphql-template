package com.redbubble.gql.graphql

import com.redbubble.gql.backends._

/**
  * Limit/offset based pagination. Used for both incoming pagination (client wants some results), in which case
  * `nextOffset` is `None`, as well as outgoing pagination (this is how many results are available), in which case
  * `nextOffset` is `Some`, which indicates to the client what the next offset should be that it requests.
  */
final case class Pagination(limit: Limit, currentOffset: Offset, nextOffset: Option[Offset] = None) {
  /**
    * For outgoing pagination, are there more results available. If so, use `nextOffset` to specify the start of the
    * next page.
    */
  def moreResultsAvailable: Boolean = nextOffset.exists(next => next > currentOffset)

  /**
    * Map this limit/offset based pagination to an offset based one.
    */
  def asPage(perPage: ResultsPerPage): Page = {
    val page =
      if (currentOffset < perPage) {
        1
      } else {
        (currentOffset / perPage) + 1
      }
    Page(page)
  }

  /**
    * Map the results of a page-based pagination response back into a limit/offset based one.
    */
  def fromPageData(currentPage: Page, totalPages: TotalPages, resultsPerPage: ResultsPerPage): Pagination = {
    val nextOffset =
      if (currentPage < totalPages) {
        currentOffset + resultsPerPage
      } else {
        currentOffset
      }
    copy(nextOffset = Some(Offset(nextOffset)))
  }

}
