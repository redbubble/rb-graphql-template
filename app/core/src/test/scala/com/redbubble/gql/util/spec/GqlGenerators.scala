package com.redbubble.gql.util.spec

import com.redbubble.util.spec.gen.{GenHelpers, JsonGenerators, StdLibGenerators}

trait GqlGenerators extends StdLibGenerators
    with JsonGenerators
    with GenHelpers
    with PeopleGenerators

object GqlGenerators extends GqlGenerators
