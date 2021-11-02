package booklet.services.database

import booklet.model._
import booklet.{Config, Failure}
import cats.effect.{ContextShift, IO}
import doobie.implicits._
import doobie.util.Get
import doobie.util.fragments.setOpt
import doobie.{Put, Read, Transactor}
import zio._
import zio.interop.catz._

import java.sql
import java.time.LocalDate
import java.util.Date
import scala.concurrent.ExecutionContext

object DatabaseLive {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  implicit val dateGet: Get[LocalDate] =
    Get[Date].map(date => new sql.Date(date.getTime).toLocalDate)

  implicit val datePut: Put[LocalDate] =
    Put[Date].contramap(date => sql.Date.valueOf(date))

  implicit val bookRead: Read[Book] =
    Read[(Long, String, String, String)].map { case (id, isbn, author, title) =>
      Book(BookId(id), Isbn(isbn), Author(author), Title(title), None, None, None)
    }

  implicit val readingRead: Read[Reading] =
    Read[(Long, Book, LocalDate, Int)].map { case (id, book, completed, rating) =>
      Reading(ReadingId(id), book, completed, Rating(rating))
    }

  val layer: ZLayer[Has[Config], Failure, Has[Database]] = {
    val effect = for {
      config <- ZIO.service[Config]
    } yield {
      val xa = {
        val jdbc = JdbcConfig.fromDbUrl(config.db.url)
        Transactor
          .fromDriverManager[Task](
            driver = config.db.driver,
            url = jdbc.url,
            user = jdbc.userName.getOrElse(""),
            pass = jdbc.password.getOrElse("")
          )
      }
      toDatabase(xa)
    }
    effect.toLayer
  }

  private def toDatabase(xa: Transactor[Task]): Database =
    new Database {

      val fetchAllBooks: ZIO[Any, Failure, List[Book]] =
        sql"""
             |SELECT id, isbn, author, title
             |FROM books
             |""".stripMargin
          .query[Book]
          .to[List]
          .transact(xa)
          .mapError(Failure.fromThrowable)

      val fetchAllReadings: ZIO[Any, Failure, List[Reading]] =
        sql"""
             |SELECT r.id, b.id, b.isbn, b.author, b.title, r.completed, r.rating
             |FROM books b
             |JOIN readings r 
             |ON r.book_id = b.id
             |""".stripMargin
          .query[Reading]
          .to[List]
          .transact(xa)
          .mapError(Failure.fromThrowable)

      def fetchBook(id: BookId): ZIO[Any, Failure, Option[Book]] =
        sql"""
             |SELECT id, isbn, author, title
             |FROM books
             |WHERE id = $id

             |""".stripMargin
          .query[Book]
          .option
          .transact(xa)
          .mapError(Failure.fromThrowable)

      def fetchReading(id: ReadingId): ZIO[Any, Failure, Option[Reading]] =
        sql"""
             |SELECT r.id, b.id, b.isbn, b.author, b.title, r.completed, r.rating
             |FROM books b
             |JOIN readings r 
             |ON r.book_id = b.id
             |WHERE r.id = $id
             |""".stripMargin
          .query[Reading]
          .option
          .transact(xa)
          .mapError(Failure.fromThrowable)

      def insertBook(data: BookData): ZIO[Any, Failure, Unit] =
        sql"""
             |INSERT INTO books(isbn, author, title)
             |VALUES (${data.isbn}, ${data.author}, ${data.title})
             |""".stripMargin.update
          .withUniqueGeneratedKeys[Long]("id")
          .transact(xa)
          .mapBoth(
            Failure.fromThrowable,
            _ => ()
          )

      def insertReading(data: ReadingData): ZIO[Any, Failure, Unit] =
        sql"""
             |INSERT INTO readings(book_id, completed, rating)
             |VALUES (${data.bookId}, ${data.completed}, ${data.rating})
             |""".stripMargin.update
          .withUniqueGeneratedKeys[Long]("id")
          .transact(xa)
          .mapBoth(
            Failure.fromThrowable,
            _ => ()
          )

      def updateBook(id: BookId, data: BookData): ZIO[Any, Failure, Unit] = {
        val q = fr"UPDATE books" ++ setOpt(
          data.isbn.map(isbn => fr"isbn = ${isbn.value}"),
          data.author.map(author => fr"author = ${author.value}"),
          data.title.map(title => fr"title = ${title.value}")
        ) ++ fr"WHERE id=$id"

        q.update.run
          .transact(xa)
          .mapBoth(
            Failure.fromThrowable,
            _ => ()
          )
      }

      def updateReading(id: ReadingId, data: ReadingData): ZIO[Any, Failure, Unit] = {
        val q = fr"UPDATE readings" ++ setOpt(
          data.completed.map(completed => fr"completed = ${completed.toString}"),
          data.rating.map(rating => fr"rating = ${rating.value}")
        ) ++ fr"WHERE id=$id"

        q.update.run
          .transact(xa)
          .mapBoth(
            Failure.fromThrowable,
            _ => ()
          )
      }

      def deleteBook(id: BookId): ZIO[Any, Failure, Unit] =
        sql"""
             |DELETE FROM books
             |WHERE id = $id
             |""".stripMargin.update.run
          .transact(xa)
          .mapBoth(
            Failure.fromThrowable,
            _ => ()
          )

      def deleteReading(id: ReadingId): ZIO[Any, Failure, Unit] =
        sql"""
           |DELETE FROM readings
           |WHERE id = $id
           |""".stripMargin.update.run
          .transact(xa)
          .mapBoth(
            Failure.fromThrowable,
            _ => ()
          )
    }
}
