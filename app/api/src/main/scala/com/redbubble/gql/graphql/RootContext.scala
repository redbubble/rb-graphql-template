package com.redbubble.gql.graphql

import com.redbubble.gql.services.people.PeopleService

trait RootContext extends PeopleService

object RootContext extends RootContext
