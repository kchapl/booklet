package services.database

import cats.effect.{ContextShift, IO}
import config.Config.config
import doobie._
import doobie.implicits._
import doobie.util.Get
import doobie.util.fragment.Fragment
import model.{Book, Reading}
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
      Read[(String, String)].map { case (author, title) => Book(author, title) }

    implicit val readingRead: Read[Reading] =
      Read[(Book, LocalDate, Int)].map { case (book, completed, rating) =>
        Reading(book, completed, rating)
      }

    object Queries {

      val fetchAllReadings: Query0[Reading] =
        sql"""SELECT 
            b.author, 
            b.title, 
            r.completed, 
            r.rating 
           FROM readings r JOIN books b ON r.book_id = b.id"""
          .query[Reading]

      val fetchLastKey: Query0[Long] = sql"SELECT lastval()".query[Long]

      def insertBook(book: Book): Fragment =
        sql"""INSERT INTO books(author, title)
             VALUES (${book.author},${book.title})"""

      def insertReading(bookId: Long, reading: Reading): Fragment =
        sql"""INSERT INTO readings(book_id, completed, rating)
             VALUES ($bookId, ${reading.completed}, ${reading.rating})"""
    }

    ZLayer.succeed(new Service {

      override def fetchAllReadings(): Task[List[Reading]] =
        Queries.fetchAllReadings.to[List].transact(xa)

      def insertBook(book: Book): ConnectionIO[Int] = Queries.insertBook(book).update.run

      override def insertReading(reading: Reading): Task[Unit] =
        (for {
          _      <- insertBook(reading.book)
          bookId <- Queries.fetchLastKey.unique
          _      <- Queries.insertReading(bookId, reading).update.run
        } yield ()).transact(xa)
    })
  }
}
