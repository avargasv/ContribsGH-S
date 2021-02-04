package code.model

import net.liftweb.json.JsonDSL._

object Entities {

  // entities of the REST service

  type Organization = String

  case class Repository(name: String) extends AnyVal

  case class Contributor(name: String, contributions: Int) {
    def asJson = {
        ("name" -> name) ~
        ("contributions" -> contributions)
    }
  }

  case class Contribution(
    login: String,
    id: Int,
    node_id: String,
    avatar_url: String,
    gravatar_id: String,
    url: String,
    html_url: String,
    followers_url: String,
    following_url: String,
    gists_url: String,
    starred_url: String,
    subscriptions_url: String,
    organizations_url: String,
    repos_url: String,
    events_url: String,
    received_events_url: String,
    `type`: String,
    site_admin: Boolean,
    contributions: Int
  )

  // auxiliary types for the REST client

  type Body = String
  type Error = String

}