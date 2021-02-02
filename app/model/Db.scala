package model

import cats.effect.{ContextShift, IO}
import config.Config.config
import doobie.Transactor
import zio.Task
import zio.interop.catz._

import scala.concurrent.ExecutionContext

object Db {

  private implicit val cs: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  val xa: Transactor[Task] = Transactor.fromDriverManager[Task](
    driver = config.dbDriver,
    url = config.dbUrl,
    user = config.dbUser,
    pass = config.dbPass
  )
}
