package model

import cats.effect.{ContextShift, IO}
import config.Config.config
import doobie.{Put, Transactor}
import doobie.util.Get
import zio.Task
import zio.interop.catz._

import java.sql
import java.time.LocalDate
import java.util.Date
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

  implicit val dateGet: Get[LocalDate] =
    Get[Date].map(date => new sql.Date(date.getTime).toLocalDate)

  implicit val datePut: Put[LocalDate] =
    Put[Date].contramap(date => sql.Date.valueOf(date))
}
