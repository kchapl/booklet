package booklet.impure.service

import booklet.pure.Failure
import booklet.pure.model.{BookData, BookId, ReadingData, ReadingId}
import zio.{IO, Task, ZIO}

trait GoogleSheetsService {
  def createSheet(userId: String): IO[Failure, String]
  def fetchSheetId(userId: String): IO[Failure, Option[String]]
  def insertBook(userId: String, data: BookData): IO[Failure, Unit]
  def updateBook(userId: String, id: BookId, data: BookData): IO[Failure, Unit]
  def deleteBook(userId: String, id: BookId): IO[Failure, Unit]
  def insertReading(userId: String, data: ReadingData): IO[Failure, Unit]
  def updateReading(userId: String, id: ReadingId, data: ReadingData): IO[Failure, Unit]
  def deleteReading(userId: String, id: ReadingId): IO[Failure, Unit]
}

object GoogleSheetsService {
  def createSheet(userId: String): ZIO[GoogleSheetsService, Failure, String] =
    ZIO.serviceWithZIO(_.createSheet(userId))

  def fetchSheetId(userId: String): ZIO[GoogleSheetsService, Failure, Option[String]] =
    ZIO.serviceWithZIO(_.fetchSheetId(userId))

  def insertBook(userId: String, data: BookData): ZIO[GoogleSheetsService, Failure, Unit] =
    ZIO.serviceWithZIO(_.insertBook(userId, data))

  def updateBook(
      userId: String,
      id: BookId,
      data: BookData
  ): ZIO[GoogleSheetsService, Failure, Unit] =
    ZIO.serviceWithZIO(_.updateBook(userId, id, data))

  def deleteBook(userId: String, id: BookId): ZIO[GoogleSheetsService, Failure, Unit] =
    ZIO.serviceWithZIO(_.deleteBook(userId, id))

  def insertReading(userId: String, data: ReadingData): ZIO[GoogleSheetsService, Failure, Unit] =
    ZIO.serviceWithZIO(_.insertReading(userId, data))

  def updateReading(
      userId: String,
      id: ReadingId,
      data: ReadingData
  ): ZIO[GoogleSheetsService, Failure, Unit] =
    ZIO.serviceWithZIO(_.updateReading(userId, id, data))

  def deleteReading(userId: String, id: ReadingId): ZIO[GoogleSheetsService, Failure, Unit] =
    ZIO.serviceWithZIO(_.deleteReading(userId, id))
}

object GoogleSheetsServiceLive {
  val layer: ZLayer[Any, Nothing, GoogleSheetsService] = ZLayer.succeed(new GoogleSheetsService {
    override def createSheet(userId: String): IO[Failure, String] =
      // Implement the logic to create a new Google sheet for the user
      ???

    override def fetchSheetId(userId: String): IO[Failure, Option[String]] =
      // Implement the logic to fetch the Google sheet ID for the user
      ???

    override def insertBook(userId: String, data: BookData): IO[Failure, Unit] =
      // Implement the logic to insert a book record into the user's Google sheet
      ???

    override def updateBook(userId: String, id: BookId, data: BookData): IO[Failure, Unit] =
      // Implement the logic to update a book record in the user's Google sheet
      ???

    override def deleteBook(userId: String, id: BookId): IO[Failure, Unit] =
      // Implement the logic to delete a book record from the user's Google sheet
      ???

    override def insertReading(userId: String, data: ReadingData): IO[Failure, Unit] =
      // Implement the logic to insert a reading record into the user's Google sheet
      ???

    override def updateReading(
        userId: String,
        id: ReadingId,
        data: ReadingData
    ): IO[Failure, Unit] =
      // Implement the logic to update a reading record in the user's Google sheet
      ???

    override def deleteReading(userId: String, id: ReadingId): IO[Failure, Unit] =
      // Implement the logic to delete a reading record from the user's Google sheet
      ???
  })
}
