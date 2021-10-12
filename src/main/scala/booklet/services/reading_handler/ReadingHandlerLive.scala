package booklet.services.reading_handler

import booklet.http.CustomResponse._
import booklet.model.ReadingId
import booklet.services.database.Database
import booklet.views.ReadingView
import zhttp.http.UResponse
import zio.{Has, UIO, URLayer, ZIO}

object ReadingHandlerLive {

  val layer: URLayer[Has[Database], Has[ReadingHandler]] =
    ZIO.service[Database].map(toReadingHandler).toLayer

  private def toReadingHandler(db: Database): ReadingHandler =
    new ReadingHandler {
      val fetchAll: UIO[UResponse] = fetchAllFrom(db)

      def fetch(readingId: String): UIO[UResponse] = fetchFrom(db)(readingId)
    }

  private def toReadingId(readingId: String): ZIO[Any, Option[Nothing], ReadingId] =
    ZIO
      .fromOption(readingId.toLongOption)
      .map(ReadingId)

  private def fetchAllFrom(db: Database) =
    db.fetchAllReadings
      .fold(
        serverFailure,
        readings => ok(ReadingView.list(readings).toString)
      )

  private def fetchFrom(db: Database)(readingId: String) =
    toReadingId(readingId)
      .foldM(
        _ => ZIO.succeed(badRequest(s"Cannot parse ID $readingId")),
        id =>
          db
            .fetchReading(id)
            .fold(
              serverFailure,
              {
                case None          => notFound(s"No such reading: $readingId")
                case Some(reading) => ok(ReadingView.list(Seq(reading)).toString)
              }
            )
      )
}
