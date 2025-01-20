package booklet.impure.service

import booklet.pure.Failure
import booklet.pure.http.CustomResponse._
import booklet.pure.http.Query
import booklet.pure.model.{ReadingData, ReadingId}
import booklet.pure.views.ReadingView
import zhttp.http.Path.Segment
import zhttp.http.{Body, Path, Request, Response}
import zio._

trait ReadingHandler {
  def fetchAll(userId: String): IO[Failure, Response]

  def fetch(readingId: String, userId: String): IO[Failure, Response]

  def create(request: Request, userId: String): IO[Failure, Response]

  def update(readingId: String, request: Request, userId: String): IO[Failure, Response]

  def delete(readingId: String, userId: String): IO[Failure, Response]
}

object ReadingHandler {
  def fetchAll(userId: String): ZIO[ReadingHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.fetchAll(userId))

  def fetch(readingId: String, userId: String): ZIO[ReadingHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.fetch(readingId, userId))

  def create(request: Request, userId: String): ZIO[ReadingHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.create(request, userId))

  def update(
      readingId: String,
      request: Request,
      userId: String
  ): ZIO[ReadingHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.update(readingId, request, userId))

  def delete(readingId: String, userId: String): ZIO[ReadingHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.delete(readingId, userId))
}

object ReadingHandlerLive {

  private val readingsPath = "/readings"

  private def toReadingId(readingId: String) =
    ZIO
      .fromOption(readingId.toLongOption)
      .map(ReadingId(_))

  private def fetchAllFrom(db: GoogleSheetsService, userId: String) =
    db.fetchAllReadings(userId)
      .fold(
        serverFailure,
        readings => ok(Body.fromString(ReadingView.list(readings)))
      )

  private def fetchFrom(db: GoogleSheetsService, readingId: String, userId: String) =
    toReadingId(readingId)
      .foldZIO(
        _ => ZIO.succeed(badRequest(s"Cannot parse ID $readingId")),
        id =>
          db
            .fetchReading(id, userId)
            .fold(
              serverFailure,
              {
                case None          => notFound(Path(Vector(Segment(readingId))))
                case Some(reading) => ok(Body.fromString(ReadingView.list(Seq(reading))))
              }
            )
      )

  private def createFrom(db: GoogleSheetsService, request: Request, userId: String) =
    Query
      .fromRequest(request)
      .flatMap(requestQry =>
        ZIO
          .fromOption(ReadingData.completeFromHttpQuery(requestQry))
          .foldZIO(
            _ => ZIO.succeed(badRequest(requestQry.toString)),
            readingData =>
              db
                .insertReading(readingData.copy(userId = Some(userId)), userId)
                .fold(
                  serverFailure,
                  _ => seeOther(readingsPath)
                )
          )
      )

  private def updateFrom(
      db: GoogleSheetsService,
      readingId: String,
      request: Request,
      userId: String
  ) =
    Query
      .fromRequest(request)
      .flatMap(requestQry =>
        toReadingId(readingId)
          .foldZIO(
            _ => ZIO.succeed(badRequest(requestQry.toString)),
            id =>
              db
                .updateReading(id, ReadingData.partialFromHttpQuery(requestQry), userId)
                .fold(
                  serverFailure,
                  _ => seeOther(readingsPath)
                )
          )
      )

  private def deleteFrom(db: GoogleSheetsService, readingId: String, userId: String) =
    toReadingId(readingId)
      .foldZIO(
        _ => ZIO.succeed(badRequest(s"Cannot parse ID $readingId")),
        id =>
          db
            .deleteReading(id, userId)
            .fold(
              serverFailure,
              _ => seeOther(readingsPath)
            )
      )

  private def fromDatabase(db: GoogleSheetsService) =
    new ReadingHandler {
      override def fetchAll(userId: String): UIO[Response] = fetchAllFrom(db, userId)

      override def fetch(readingId: String, userId: String): UIO[Response] =
        fetchFrom(db, readingId, userId)

      override def create(request: Request, userId: String): IO[Failure, Response] =
        createFrom(db, request, userId)

      override def update(
          readingId: String,
          request: Request,
          userId: String
      ): IO[Failure, Response] =
        updateFrom(db, readingId, request, userId)

      override def delete(readingId: String, userId: String): UIO[Response] =
        deleteFrom(db, readingId, userId)
    }

  val layer: URLayer[GoogleSheetsService, ReadingHandler] =
    ZLayer.fromZIO(ZIO.service[GoogleSheetsService].map(fromDatabase))
}
