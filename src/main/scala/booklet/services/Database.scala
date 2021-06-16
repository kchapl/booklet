package booklet.services

import booklet.Config.config
import booklet.model._
import cats.effect.{ContextShift, IO}
import doobie._
import doobie.implicits._
import doobie.util.Get
import zio._
import zio.interop.catz._

import java.sql
import java.time.LocalDate
import java.util.Date
import scala.concurrent.ExecutionContext

object Database {

  type Database = Has[Database.Service]

  trait Service {
    def fetchAllBooks(): ZIO[Any, Throwable, List[Book]]

    def createBook(data: BookData): ZIO[Any, Throwable, Book]
  }

  def fetchAllBooks(): ZIO[Database, Throwable, List[Book]] =
    ZIO.accessM(_.get.fetchAllBooks())

  def createBook(data: BookData): ZIO[Database, Throwable, Book] =
    ZIO.accessM(_.get.createBook(data))

  val live: ZLayer[Any, Nothing, Database] = {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

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

    implicit val bookRead: Read[Book] =
      Read[(Long, String, String, String)].map { case (id, isbn, author, title) =>
        Book(id, Isbn(isbn), Author(author), Title(title), None, None, None)
      }

    implicit val readingRead: Read[Reading] =
      Read[(Long, Book, LocalDate, Int)].map { case (id, book, completed, rating) =>
        Reading(id, book, completed, Rating(rating))
      }

    object Queries {
      val fetchAllBooks =
        sql"""SELECT 
              b.id,
              b.isbn,
              b.author, 
              b.title
              FROM books b"""
          .query[Book]
    }

    ZLayer.succeed(new Service {
      def fetchAllBooks(): ZIO[Any, Throwable, List[Book]] =
        Queries.fetchAllBooks.to[List].transact(xa)

      def createBook(data: BookData): ZIO[Any, Throwable, Book] = ???
    })
  }
}
