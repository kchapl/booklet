package model

import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.{Query0, Read}
import model.Db.xa
import zio.Task
import zio.interop.catz._

case class Book(author: String, title: String)

object Book {

  implicit val bookRead: Read[Book] =
    Read[(String, String)].map { case (author, title) => Book(author, title) }

  def fetchAll(): Task[List[Book]] =
    Queries.fetchAll.to[List].transact(xa)

  def insert(book: Book): doobie.ConnectionIO[Int] =
    Queries.insert(book).update.run

  object Queries {
    val fetchAll: Query0[Book] =
      sql"select author, title from books"
        .query[Book]

    def insert(book: Book): Fragment =
      sql"INSERT INTO books(author, title) values (${book.author},${book.title})"
  }
}
