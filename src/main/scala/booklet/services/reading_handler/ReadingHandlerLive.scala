package booklet.services.reading_handler

import booklet.http.CustomResponse._
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
    }

  private def fetchAllFrom(db: Database) =
    db.fetchAllReadings
      .fold(
        serverFailure,
        readings => ok(ReadingView.list(readings).toString)
      )
}
