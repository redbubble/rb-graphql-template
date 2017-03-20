package com.redbubble.gql.graphql.schema.types

import com.redbubble.graphql.ScalarTypes.{intScalarType, intValueFromInt}
import com.redbubble.gql.backends.{Limit, Offset, Page}
import com.redbubble.gql.graphql.Pagination
import com.redbubble.gql.graphql.schema.{DefaultLimit, DefaultOffset, DefaultPage}
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema.{Argument, BooleanType, Field, ObjectType, OptionInputType, OptionType, fields}
import sangria.validation.ValueCoercionViolation

object PaginationTypes {
  //
  // Limit
  //

  private val limitRange = 1 until Int.MaxValue
  private implicit val limitInput = new ScalarToInput[Limit]

  private def parseLimit(c: Int) = intValueFromInt(c, limitRange, Limit, () => LimitCoercionViolation)

  private case object LimitCoercionViolation
      extends ValueCoercionViolation(s"Limit value expected, between ${limitRange.start} and ${limitRange.end}")

  val LimitType = intScalarType(
    "Limit",
    s"The number of results required, between ${limitRange.start} and ${limitRange.end} (default $DefaultLimit).",
    parseLimit, () => LimitCoercionViolation)

  val LimitArg = Argument(
    "limit", OptionInputType(LimitType),
    s"The number of results required, between ${limitRange.start} and ${limitRange.end} (default $DefaultLimit).", DefaultLimit)

  //
  // Offset
  //

  private val offsetRange = 0 until Int.MaxValue
  private implicit val offsetInput = new ScalarToInput[Offset]

  private def parseOffset(c: Int) = intValueFromInt(c, offsetRange, Offset, () => OffsetCoercionViolation)

  private case object OffsetCoercionViolation
      extends ValueCoercionViolation(s"Offset value expected, between ${offsetRange.start} and ${offsetRange.end}")

  val OffsetType = intScalarType(
    "Offset",
    s"An offset into the results, between ${offsetRange.start} and ${offsetRange.end} (default $DefaultOffset).",
    parseOffset, () => OffsetCoercionViolation)

  val OffsetArg = Argument("offset",
    OptionInputType(OffsetType),
    s"An offset into the results, between ${offsetRange.start} and ${offsetRange.end} (default $DefaultOffset).", DefaultOffset)

  //
  // Pagination
  //

  val PaginationType = ObjectType(
    "Pagination", "Details about the pagination options available for a list of items.",
    fields[Unit, Pagination](
      Field("currentOffset", OffsetType, Some("The offset for the current set of results."),
        resolve = _.value.currentOffset
      ),
      Field("nextOffset", OptionType(OffsetType), Some("The offset for the start of the next set of results."),
        resolve = ctx => ctx.value.nextOffset.getOrElse(ctx.value.currentOffset)
      ),
      Field("moreResults", BooleanType, Some("Whether there are more results available."),
        resolve = _.value.moreResultsAvailable
      )
    )
  )
}
