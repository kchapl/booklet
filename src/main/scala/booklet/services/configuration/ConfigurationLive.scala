package booklet.services.configuration

import booklet.{Config, Failure}
import zio._

object ConfigurationLive {

  val layer: Layer[Failure, Has[Configuration]] =
    Config.load.map { config =>
      new Configuration {
        val get: UIO[Config] = UIO.succeed(config)
      }
    }.toLayer
}
