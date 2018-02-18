package model

import slick.basic.DatabaseConfig
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.Future

case class Book(id: Int, author: String, title: String)

object Book {

  def tupled = (Book.apply _).tupled

  class Books(tag: Tag) extends Table[Book](tag, "BOOKS") {
    def id     = column[Int]("ID", O.PrimaryKey)
    def author = column[String]("AUTHOR")
    def title  = column[String]("TITLE")
    def *      = (id, author, title) <> (Book.tupled, Book.unapply)
  }
  val books = TableQuery[Books]

  def setup()(implicit dbConfig: DatabaseConfig[H2Profile]): Future[Unit] = {
    val config = DBIO.seq(
      books.schema.create,
      books += Book(101, "Acme, Inc.", "99 Market Street"),
      books += Book(49, "Superior Coffee", "1 Party Place"),
      books += Book(150, "The High Ground", "100 Coffee Lane")
    )
    dbConfig.db.run(config)
  }

  def fetchAll()(implicit dbConfig: DatabaseConfig[H2Profile]): Future[Seq[Book]] = {
    dbConfig.db.run(books.result)
  }
}
