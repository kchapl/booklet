package booklet.services.database

import booklet.Failure
import booklet.model._
import zio._

trait Database {
  val fetchAllBooks: ZIO[Any, Failure, List[Book]]

  def fetchBook(id: BookId): ZIO[Any, Failure, Option[Book]]

  def insertBook(data: BookData): ZIO[Any, Failure, Unit]

  def updateBook(id: BookId, data: BookData): ZIO[Any, Failure, Unit]

  def deleteBook(id: BookId): ZIO[Any, Failure, Unit]
}

object Database {

  val fetchAllBooks: ZIO[Has[Database], Failure, List[Book]] =
    ZIO.serviceWith(_.fetchAllBooks)

  def fetchBook(id: BookId): ZIO[Has[Database], Failure, Option[Book]] =
    ZIO.serviceWith(_.fetchBook(id))

  def insertBook(data: BookData): ZIO[Has[Database], Failure, Unit] =
    ZIO.serviceWith(_.insertBook(data))

  def updateBook(id: BookId, data: BookData): ZIO[Has[Database], Failure, Unit] =
    ZIO.serviceWith(_.updateBook(id, data))

  def deleteBook(id: BookId): ZIO[Has[Database], Failure, Unit] =
    ZIO.serviceWith(_.deleteBook(id))
}
