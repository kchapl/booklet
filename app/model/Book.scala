package model

import anorm.SqlParser._
import anorm._
import play.api.db.Database

case class Book(id: String, author: String, title: String)

object Book {

  private val parser =
    str("id") ~ str("author") ~ str("title") map to(Book.apply _)

  def all(implicit db: Database): Seq[Book] = {
    db.withConnection { implicit conn =>
      SQL("Select * from Books").as(parser.*)
    }
  }
}
