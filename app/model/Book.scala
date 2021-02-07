package model

import doobie.implicits._
import model.Db.xa
import zio.Task
import zio.interop.catz._

case class Book(id: Int, author: String, title: String)

object Book {

  def fetchAll(): Task[List[Book]] =
    Queries.fetchAll.to[List].transact(xa)

  object Queries {
    val fetchAll: doobie.Query0[Book] =
      sql"select id, author, title from books"
        .query[Book]
  }
}
