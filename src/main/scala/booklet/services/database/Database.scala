package booklet.services.database

import booklet.Config.config
import booklet.Failure
import booklet.model._
import cats.effect.{ContextShift, IO}
import doobie.implicits._
import doobie.util.Get
import doobie.{Put, Read, Transactor}
import zio.interop.catz._
import zio.{Has, Task, ZIO, ZLayer}

import java.sql
import java.time.LocalDate
import java.util.Date
import scala.concurrent.ExecutionContext

object Database {

  type Database = Has[Database.Service]

  trait Service {
    def fetchAllBooks(): ZIO[Any, Failure, List[Book]]
    def insertBook(data: BookData): ZIO[Any, Failure, Unit]
    def deleteBook(id: Id): ZIO[Any, Failure, Unit]
  }

  def fetchAllBooks(): ZIO[Database, Failure, List[Book]] =
    ZIO.serviceWith(_.fetchAllBooks())

  def insertBook(data: BookData): ZIO[Database, Failure, Unit] =
    ZIO.serviceWith(_.insertBook(data))

  val live: ZLayer[Any, Nothing, Database] = {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val xa: Transactor[Task] = {
      val jdbc = JdbcConfig.fromDbUrl(config.dbUrl)
      Transactor.fromDriverManager[Task](
        driver = config.dbDriver,
        url = jdbc.url,
        user = jdbc.userName.getOrElse(""),
        pass = jdbc.password.getOrElse("")
      )
    }

    implicit val dateGet: Get[LocalDate] =
      Get[Date].map(date => new sql.Date(date.getTime).toLocalDate)

    implicit val datePut: Put[LocalDate] =
      Put[Date].contramap(date => sql.Date.valueOf(date))

    implicit val bookRead: Read[Book] =
      Read[(Long, String, String, String)].map { case (id, isbn, author, title) =>
        Book(id, Isbn(isbn), Author(author), Title(title), None, None, None)
      }

    implicit val readingRead: Read[Reading] =
      Read[(Long, Book, LocalDate, Int)].map { case (id, book, completed, rating) =>
        Reading(id, book, completed, Rating(rating))
      }

    ZLayer.succeed(new Service {
      def fetchAllBooks(): ZIO[Any, Failure, List[Book]] =
        sql"""SELECT id, isbn, author, title FROM books"""
          .query[Book]
          .to[List]
          .transact(xa)
          .mapError(Failure(_))

      def insertBook(data: BookData): ZIO[Any, Failure, Unit] =
        sql"INSERT INTO books(isbn, author, title) VALUES (${data.isbn}, ${data.author}, ${data.title})".update
          .withUniqueGeneratedKeys[Long]("id")
          .transact(xa)
          .bimap(
            Failure(_),
            _ => ()
          )

      def deleteBook(id: Id): ZIO[Any, Failure, Unit] =
        sql"DELETE FROM books WHERE id = ${id.value}".update.run
          .transact(xa)
          .bimap(
            Failure(_),
            _ => ()
          )
    })
  }
}
