package booklet.services.configuration

import booklet.{Config, Failure}
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._
import zio._

trait Configuration {
  val load: ZIO[Any, Failure, Config]
}

object Configuration {

  val load: ZIO[Has[Configuration], Failure, Config] = ZIO.serviceWith(_.load)

  val live: ULayer[Has[Configuration]] =
    ZLayer.succeed(new Configuration {
      val load = ZIO.fromEither(
        ConfigSource.default
          .load[Config]
          .left
          .map(failures => Failure(ConfigReaderException(failures)))
      )
    })
}
