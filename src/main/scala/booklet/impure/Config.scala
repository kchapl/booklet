package booklet.impure

import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._
import zio.{IO, Layer, URIO, ZIO, ZLayer}

case class AppConfig(
    port: Int
)

case class BookLookupConfig(
    url: String,
    key: String,
    signInClientId: String
)

case class GoogleSheetsConfig(
    clientId: String,
    clientSecret: String
)

case class Config(
    app: AppConfig,
    bookLookup: BookLookupConfig,
    googleSheets: GoogleSheetsConfig
)

object Config {
  val service: URIO[Config, Config] = ZIO.service[Config]
}

object ConfigLive {
  private val load: IO[ConfigReaderFailures, Config] =
    ZIO.fromEither(ConfigSource.default.load[Config])
  val layer: Layer[ConfigReaderFailures, Config] = ZLayer.fromZIO(load)
}
