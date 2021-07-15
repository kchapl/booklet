package booklet

import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class Config(
    port: Int,
    dbDriver: String,
    dbUrl: String
)

object Config {
  lazy val config: Config = ConfigSource.default.loadOrThrow[Config]
}
