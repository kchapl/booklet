package config

import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class Config(
    dbDriver: String,
    dbUrl: String,
    dbUser: String,
    dbPass: String
)

object Config {
  lazy val config: Config = ConfigSource.default.loadOrThrow[Config]
}
