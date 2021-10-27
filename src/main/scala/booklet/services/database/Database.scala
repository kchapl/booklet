package booklet.services.database

import booklet.Failure
import booklet.model._
import zio._

trait Database {
  val fetchAllBooks: IO[Failure, List[Book]]
  val fetchAllReadings: IO[Failure, List[Reading]]

  def fetchBook(id: BookId): IO[Failure, Option[Book]]

  def fetchReading(id: ReadingId): IO[Failure, Option[Reading]]

  def insertBook(data: BookData): IO[Failure, Unit]

  def insertReading(data: ReadingData): IO[Failure, Unit]

  def updateBook(id: BookId, data: BookData): IO[Failure, Unit]

  def updateReading(id: ReadingId, data: ReadingData): IO[Failure, Unit]

  def deleteBook(id: BookId): IO[Failure, Unit]

  def deleteReading(id: ReadingId): IO[Failure, Unit]
}
