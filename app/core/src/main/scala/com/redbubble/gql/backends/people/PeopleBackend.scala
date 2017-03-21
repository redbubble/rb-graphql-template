package com.redbubble.gql.backends.people

import com.redbubble.gql.services.people.{Person, PersonId}
import com.redbubble.gql.util.config.Environment.env
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.syntax._
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, RelativePath}

trait PeopleBackend {
  protected val peopleApiClient: JsonApiClient

  private implicit val decodePeople = PeopleDecoders.peopleDecoder
  private implicit val decodePerson = PeopleDecoders.personDecoder

  final def allPeople(): DownstreamResponse[Seq[Person]] =
    peopleApiClient.get[Seq[Person]](RelativePath("people")).empty

  final def personDetails(personId: PersonId): DownstreamResponse[Option[Person]] =
    peopleApiClient.get[Option[Person]](RelativePath(s"people/$personId")).empty
}

object PeopleBackend extends PeopleBackend {
  protected override val peopleApiClient = client(env.peopleApiUrl)(clientMetrics)
}

