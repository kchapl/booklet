package model

import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.{Query0, Read}
import model.Db._
import zio.Task
import zio.interop.catz._

import java.time.LocalDate

case class Reading(book: Book, completed: LocalDate, rating: Int)

object Reading {

  implicit val readingRead: Read[Reading] =
    Read[(Book, LocalDate, Int)].map { case (book, completed, rating) =>
      Reading(book, completed, rating)
    }

  def fetchAll(): Task[List[Reading]] = Queries.fetchAll.to[List].transact(xa)

  def insert(reading: Reading): Task[Unit] =
    (for {
      _      <- Book.insert(reading.book)
      bookId <- Queries.fetchLastKey.unique
      _      <- Queries.insert(bookId, reading).update.run
    } yield ()).transact(xa)

  object Queries {

    val fetchAll: Query0[Reading] =
      sql"""SELECT 
            b.author, 
            b.title, 
            r.completed, 
            r.rating 
           FROM readings r JOIN books b ON r.book_id = b.id"""
        .query[Reading]

    val fetchLastKey: Query0[Long] = sql"SELECT lastval()".query[Long]

    def insert(bookId: Long, reading: Reading): Fragment =
      sql"""INSERT INTO readings(book_id, completed, rating) 
           VALUES ($bookId, ${reading.completed}, ${reading.rating})"""
  }
}
