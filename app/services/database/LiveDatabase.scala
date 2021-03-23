package services.database

import cats.effect.{ContextShift, IO}
import config.Config.config
import doobie._
import doobie.implicits._
import doobie.util.Get
import doobie.util.fragment.Fragment
import model.{Author, Book, BookToInsert, Isbn, Rating, Reading, ReadingToInsert, Title}
import services.database.Database.Service
import zio.interop.catz._
import zio.{Task, ULayer, ZLayer}

import java.sql
import java.time.LocalDate
import java.util.Date
import scala.concurrent.ExecutionContext

object LiveDatabase {

  val impl: ULayer[Database] = {

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

      val fetchAllReadings: Query0[Reading] =
        sql"""SELECT 
              r.id,
              b.id,
              b.isbn,
              b.author, 
              b.title,
              r.completed, 
              r.rating 
              FROM readings r 
              JOIN books b 
              ON r.book_id = b.id"""
          .query[Reading]

      val fetchLastKey: Query0[Long] = sql"SELECT lastval()".query[Long]

      def insertBook(bookToInsert: BookToInsert): Fragment =
        sql"""INSERT INTO books(isbn,author, title)
             VALUES (${bookToInsert.isbn},${bookToInsert.author},${bookToInsert.title})"""

      def deleteBook(book: Book): Fragment =
        sql"DELETE FROM books WHERE id = ${book.id}"

      def insertReading(bookId: Long, readingToInsert: ReadingToInsert): Fragment =
        sql"""INSERT INTO readings(book_id, completed, rating)
             VALUES ($bookId, ${readingToInsert.completed}, ${readingToInsert.rating})"""

      def deleteReading(reading: Reading): Fragment =
        sql"DELETE FROM readings WHERE id = ${reading.id}"
    }

    ZLayer.succeed(new Service {

      override def fetchAllReadings(): Task[List[Reading]] =
        Queries.fetchAllReadings.to[List].transact(xa)

      def insertBook(bookToInsert: BookToInsert): ConnectionIO[Int] =
        Queries.insertBook(bookToInsert).update.run

      override def insertReading(readingToInsert: ReadingToInsert): Task[Unit] =
        (for {
          _      <- insertBook(readingToInsert.bookToInsert)
          bookId <- Queries.fetchLastKey.unique
          _      <- Queries.insertReading(bookId, readingToInsert).update.run
        } yield ()).transact(xa)

      override def deleteReading(reading: Reading): Task[Unit] =
        (for {
          _ <- Queries.deleteReading(reading).update.run
          _ <- Queries.deleteBook(reading.book).update.run
        } yield ()).transact(xa)
    })
  }
}
