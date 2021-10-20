package booklet

import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._
import zio.{IO, ZIO}

case class Config(
    app: AppConfig,
    db: DbConfig,
    bookLookup: BookLookupConfig
)

object Config {
  val load: IO[Failure, Config] = ZIO.fromEither(
    ConfigSource.default
      .load[Config]
      .left
      .map(failures => Failure.fromThrowable(ConfigReaderException(failures)))
  )
}

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
