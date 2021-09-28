package booklet.services.configuration

import booklet.Config
import zio._

trait Configuration {
  val get: UIO[Config]
}

object Configuration {
  val get: URIO[Has[Configuration], Config] = ZIO.serviceWith(_.get)
}
