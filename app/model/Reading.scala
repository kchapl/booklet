package model

import doobie.Read
import doobie.implicits._
import doobie.util.Get
import model.Db.xa
import zio.Task
import zio.interop.catz._

import java.time.LocalDate

case class Reading(id: Int, book: Book, completed: LocalDate, rating: Int)

object Reading {

  implicit val dateGet: Get[LocalDate] = Get[String].map(LocalDate.parse)

  implicit val bookRead: Read[Book] =
    Read[(Int, String, String)].map { case (x, y, z) => Book(x, y, z) }

  implicit val readingRead: Read[Reading] =
    Read[(Int, Book, LocalDate, Int)].map { case (x, y, z, w) => Reading(x, y, z, w) }

  def fetchAll(): Task[List[Reading]] = Queries.fetchAll.to[List].transact(xa)

  object Queries {
    val fetchAll: doobie.Query0[Reading] =
      sql"""SELECT 
            r.id AS readingId, 
            b.id AS bookId, 
            b.author, 
            b.title, 
            r.completed, 
            r.rating 
           FROM Readings r JOIN Books b ON r.bookId = b.id"""
        .query[Reading]
  }
}
