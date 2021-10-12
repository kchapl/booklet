package booklet.services.database

import booklet.Failure
import booklet.model._
import booklet.services.configuration.Configuration
import cats.effect.{ContextShift, IO}
import cats.implicits._
import doobie.implicits._
import doobie.util.Get
import doobie.util.fragment.Fragment
import doobie.{Read, Transactor}
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

  //  implicit val datePut: Put[LocalDate] =
  //    Put[Date].contramap(date => sql.Date.valueOf(date))

  implicit val bookRead: Read[Book] =
    Read[(Long, String, String, String)].map { case (id, isbn, author, title) =>
      Book(BookId(id), Isbn(isbn), Author(author), Title(title), None, None, None)
    }

  implicit val readingRead: Read[Reading] =
    Read[(Long, Book, LocalDate, Int)].map { case (id, book, completed, rating) =>
      Reading(ReadingId(id), book, completed, Rating(rating))
    }

  val layer: ZLayer[Has[Configuration], Failure, Has[Database]] = {
    val effect = for {
      cfn <- ZIO.service[Configuration]
      config <- cfn.get
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
          .mapError(Failure.apply)

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
          .mapError(Failure.apply)

      def fetchBook(id: BookId): ZIO[Any, Failure, Option[Book]] =
        sql"""
             |SELECT id, isbn, author, title
             |FROM books
             |WHERE id = $id

             |""".stripMargin
          .query[Book]
          .option
          .transact(xa)
          .mapError(Failure.apply)

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
          .mapError(Failure.apply)

      def insertBook(data: BookData): ZIO[Any, Failure, Unit] =
        sql"""
             |INSERT INTO books(isbn, author, title)
             |VALUES (${data.isbn}, ${data.author}, ${data.title})
             |""".stripMargin.update
          .withUniqueGeneratedKeys[Long]("id")
          .transact(xa)
          .mapBoth(
            Failure.apply,
            _ => ()
          )

      def updateBook(id: BookId, data: BookData): ZIO[Any, Failure, Unit] = {
        def fieldToUpdate(name: String, value: Option[String]): Option[Fragment] =
          value.map { v =>
            Fragment.const(name) ++ fr"=$v"
          }

        val updateClause = Seq(
          fieldToUpdate("isbn", data.isbn.map(_.value)),
          fieldToUpdate("author", data.author.map(_.value)),
          fieldToUpdate("title", data.title.map(_.value))
        ).flatten.intercalate(fr",")

        fr"UPDATE books SET $updateClause WHERE id=$id".update.run
          .transact(xa)
          .mapBoth(
            Failure.apply,
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
            Failure.apply,
            _ => ()
          )
    }
}
