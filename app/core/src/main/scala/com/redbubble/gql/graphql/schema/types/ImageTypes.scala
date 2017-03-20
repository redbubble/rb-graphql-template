package com.redbubble.gql.graphql.schema.types

import com.redbubble.graphql.ScalarTypes.{intScalarType, intValueFromInt}
import com.redbubble.gql.core.ImageSize.{regularImageSize, validImageSizes}
import com.redbubble.gql.core._
import com.redbubble.gql.graphql.schema.{DefaultImageHeight, DefaultImageSizeClass, DefaultImageWidth}
import com.redbubble.gql.magickraum.MaxImageDimension
import com.redbubble.gql.services.works.WorkImage
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema._
import sangria.validation.ValueCoercionViolation

object ImageTypes {

  //
  // Width
  //

  private val widthRange = 1 to MaxImageDimension
  private implicit val widthInput = new ScalarToInput[PixelWidth]

  private case object WidthCoercionViolation
      extends ValueCoercionViolation(s"Width in pixels, between ${widthRange.start} and ${widthRange.end}")

  private def parseWidth(i: Int) = intValueFromInt(i, widthRange, PixelWidth, () => WidthCoercionViolation)

  val WidthType = intScalarType(
    "width",
    s"The width of an image, in pixels, between ${widthRange.start} and ${widthRange.end} (default $DefaultImageWidth).",
    parseWidth, () => WidthCoercionViolation)

  val WidthArg: Argument[PixelWidth] = Argument(
    name = "width",
    argumentType = OptionInputType(WidthType),
    description = s"The width of an image, in pixels, between ${widthRange.start} and ${widthRange.end} (default $DefaultImageWidth).", defaultValue = DefaultImageWidth)

  //
  // Height
  //

  private val heightRange = 1 to MaxImageDimension
  private implicit val heightInput = new ScalarToInput[PixelHeight]

  private case object HeightCoercionViolation
      extends ValueCoercionViolation(s"Height in pixels, between ${heightRange.start} and ${heightRange.end}")

  private def parseHeight(i: Int) = intValueFromInt(i, heightRange, PixelHeight, () => HeightCoercionViolation)

  val HeightType = intScalarType(
    "height",
    s"The height of an image, in pixels, between ${heightRange.start} and ${heightRange.end} (default $DefaultImageHeight).",
    parseHeight, () => HeightCoercionViolation)

  val HeightArg = Argument("height",
    OptionInputType(HeightType),
    s"The height of an image, in pixels, between ${heightRange.start} and ${heightRange.end} (default $DefaultImageHeight).", DefaultImageHeight)

  //
  // SizeClass
  //

  private implicit val sizeClassInput = new ScalarToInput[SizeClass]

  private case object SizeClassCoercionViolation
      extends ValueCoercionViolation(s"The size class of an image, one of ${validImageSizes.map(s => s.width).mkString(", ")}")

  private def parseSizeClass(i: Int) =
    regularImageSize(PixelWidth(i), PixelHeight(i)).map(s => SizeClass(s.width)).toRight(SizeClassCoercionViolation)

  val SizeClassType = intScalarType(
    "SizeClass",
    s"The size class of an image, one of ${validImageSizes.map(s => s.width).mkString(", ")} (default $DefaultImageSizeClass).",
    parseSizeClass, () => SizeClassCoercionViolation)

  val SizeClassArg = Argument("sizeClass",
    OptionInputType(SizeClassType),
    s"The size class of an image, one of ${validImageSizes.map(s => s.width).mkString(", ")} (default $DefaultImageSizeClass).", DefaultImageSizeClass)

  //
  // Images
  //

  val ImageSizeType = ObjectType(
    "ImageSize", "The size of an image.",
    fields[Unit, ImageSize](
      Field("width", WidthType, Some("The width of an image."), resolve = _.value.width),
      Field("height", HeightType, Some("The height of an image."), resolve = _.value.height)
    )
  )

  val CoordinateType = ObjectType(
    "ImageCrop", "The size of an image.",
    fields[Unit, Coordinate](
      Field("x", WidthType, Some("The X value of the coordinate, with an origin of (0,0) top-left."), resolve = _.value.x),
      Field("y", HeightType, Some("The Y value of the coordinate, with an origin of (0,0) top-left."), resolve = _.value.y)
    )
  )

  val ImageCropBoxType = ObjectType(
    "ImageCropBox", "A crop box for an image, if it requires cropping.",
    fields[Unit, ImageCropBox](
      Field("origin", CoordinateType, Some("The start/origin position of the crop, with an origin of (0,0) top-left."), resolve = _.value.origin),
      Field("width", WidthType, Some("The width of the crop box."), resolve = _.value.width),
      Field("height", HeightType, Some("The height of the crop box."), resolve = _.value.height)
    )
  )

  val ImageType = ObjectType(
    "Image", "An image.",
    fields[Unit, Image](
      Field("url", StringType, Some("The URL of the image."), resolve = _.value.url.toString),
      Field("size", OptionType(ImageSizeType), Some("The size of an image."), resolve = _.value.size),
      Field("cropBox", OptionType(ImageCropBoxType), Some("A crop box for an image, if it requires cropping."), resolve = _.value.cropBox)
    )
  )

  val WorkImageType = ObjectType(
    "WorkImage", "A work's image.",
    fields[Unit, WorkImage](
      Field("url", StringType, Some("The URL of the image."), resolve = _.value.url.toString),
      Field("size", ImageSizeType, Some("The size of an image."), resolve = _.value.size)
    )
  )
}
