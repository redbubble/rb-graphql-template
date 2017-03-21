package com.redbubble.gql.backends.people

import com.redbubble.gql.services.people.{Person, PersonId}
import com.redbubble.gql.util.config.Environment.env
import com.redbubble.gql.util.http.GqlJsonApiClient.client
import com.redbubble.gql.util.metrics.Metrics._
import com.redbubble.util.http.{DownstreamResponse, JsonApiClient, RelativePath}

trait PeopleBackend {
  protected val peopleApiClient: JsonApiClient

  private implicit val decodePerson = PersonDecoders.artistDecoder

  final def details(personId: PersonId): DownstreamResponse[Person] =
    peopleApiClient.get[Person](RelativePath(s"people/$personId"))
}

object PeopleBackend extends PeopleBackend {
  protected override val peopleApiClient = client(env.peopleApiUrl)(clientMetrics)
}

