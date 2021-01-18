package model

import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO}
import config.Config.config
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import doobie.{ConnectionIO, Transactor}

import scala.concurrent.ExecutionContext

case class Book(id: String, author: String, title: String)

object Book {

  private implicit val cs: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  private val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    driver = config.dbDriver,
    url = config.dbUrl,
    user = config.dbUser,
    pass = config.dbPass
  )

  def findAll(): IO[NonEmptyList[Book]] =
    findAllQuery.transact(xa)

  private def findAllQuery: ConnectionIO[NonEmptyList[Book]] =
    sql"select id, author, title from books"
      .query[Book]
      .nel
}
