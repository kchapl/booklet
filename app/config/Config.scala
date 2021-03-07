package config

import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class Config(
    userName: String,
    password: String,
    dbDriver: String,
    dbUrl: String,
    dbUser: String,
    dbPass: String,
    bookLookupUrl: String,
    bookLookupKey: String,
    signInClientId: String
)

object Config {
  lazy val config: Config = ConfigSource.default.loadOrThrow[Config]
}
