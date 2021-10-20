package booklet.services.database

import booklet.Failure
import booklet.model._
import zio._

trait Database {
  val fetchAllBooks: ZIO[Any, Failure, List[Book]]
  val fetchAllReadings: ZIO[Any, Failure, List[Reading]]

  def fetchBook(id: BookId): ZIO[Any, Failure, Option[Book]]
  def fetchReading(id: ReadingId): ZIO[Any, Failure, Option[Reading]]

  def insertBook(data: BookData): ZIO[Any, Failure, Unit]
  def insertReading(data: ReadingData): ZIO[Any, Failure, Unit]

  def updateBook(id: BookId, data: BookData): ZIO[Any, Failure, Unit]
  def updateReading(id: ReadingId, data: ReadingData): ZIO[Any, Failure, Unit]

  def deleteBook(id: BookId): ZIO[Any, Failure, Unit]
  def deleteReading(id: ReadingId): ZIO[Any, Failure, Unit]
}
