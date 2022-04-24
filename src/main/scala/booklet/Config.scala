package booklet

import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._
import zio.{IO, Layer, URIO, ZIO, ZLayer}

case class AppConfig(
    port: Int
)

case class DbConfig(
    driver: String,
    url: String,
    userName: String,
    password: String
)

case class BookLookupConfig(
    url: String,
    key: String,
    signInClientId: String
)

case class Config(
    app: AppConfig,
    db: DbConfig,
    bookLookup: BookLookupConfig
)

object Config {
  val service: URIO[Config, Config] = ZIO.service[Config]
}

object ConfigLive {
  private val load: IO[ConfigReaderFailures, Config] =
    ZIO.fromEither(ConfigSource.default.load[Config])
  val layer: Layer[ConfigReaderFailures, Config] = ZLayer.fromZIO(load)
}
