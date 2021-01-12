package model

import java.time.LocalDate
import java.{sql, util}

import anorm.SqlParser.{date, int, str}
import anorm._
import play.api.db.Database

case class Reading(id: String, book: Book, completed: LocalDate, rating: Int)

object Reading {

  private def toLocalDate(d: util.Date) = new sql.Date(d.getTime).toLocalDate

  private val parser =
    str("readingId") ~ date("completed") ~ int("rating") ~ str("bookId") ~ str(
      "author"
    ) ~ str("title") map {
      case readingId ~ completed ~ rating ~ bookId ~ author ~ title =>
        Reading(
          readingId,
          Book(bookId, author, title),
          toLocalDate(completed),
          rating
        )
    }

  def all(implicit db: Database): Seq[Reading] = {
    db.withConnection { implicit conn =>
      val qry =
        "select r.id as readingId, r.completed, r.rating, b.id as bookId, b.author, b.title " +
          "from Readings r join Books b on r.bookId = b.id"
      SQL(qry).as(parser.*)
    }
  }
}
