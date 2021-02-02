package model

import doobie.implicits._
import model.Db.xa
import zio.Task
import zio.interop.catz._

import java.time.LocalDate
//import java.{sql, util}

case class Reading(id: String, book: Book, completed: LocalDate, rating: Int)

object Reading {

//  private def toLocalDate(d: util.Date) = new sql.Date(d.getTime).toLocalDate

//  def fetchAll(): Task[List[Reading]] = Task(
//    List(
//      Reading("1", Book("1", "a1", "t1"), LocalDate.parse("2021-02-02"), 5),
//      Reading("2", Book("2", "a2", "t2"), LocalDate.parse("2021-01-02"), 5),
//      Reading("3", Book("1", "a1", "t1"), LocalDate.parse("2021-01-07"), 5)
//    )
//  )
  def fetchAll(): Task[List[Reading]] = Queries.fetchAll.to[List].transact(xa)

  object Queries {
    val fetchAll: doobie.Query0[Reading] =
      sql"""SELECT r.id AS readingId, r.completed, r.rating, b.id AS bookId, b.author, b.title 
           FROM Readings r JOIN Books b ON r.bookId = b.id"""
        .query[Reading]
  }
}
