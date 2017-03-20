package com.redbubble.gql.backends.artists

import com.redbubble.gql.product.Work
import com.redbubble.gql.services.artists.Artist
import com.redbubble.gql.util.spec.ModelGenerators
import com.redbubble.util.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class ArtistDecodersSpec extends Specification with SpecHelper with ModelGenerators with ArtistBackendJson {
  val decodingProp = new Properties("Artist decoding") {
    property("Works by an artist decoding") = forAll(genArtist, genWork) { (artist: Artist, work: Work) =>
      val workWithArtist = work.copy(artist = artist)
      val json = artistWorkJson(artist, Seq(workWithArtist))
      val decoded = decode(json)(ArtistDecoders.artistWorksDataDecoder)
      decoded must beRight(Seq(workWithArtist))
    }

    property("Artist details decoding") = forAll(genArtist) { (artist: Artist) =>
      val json = artistDetailsJson(Seq(artist))
      val decoded = decode(json)(ArtistDecoders.artistDataDecoder)
      decoded must beRight(Seq(artist))
    }
  }

  s2"Artist API decoding$decodingProp"
}
