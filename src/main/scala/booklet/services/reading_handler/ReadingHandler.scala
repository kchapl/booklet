package booklet.services.reading_handler

import booklet.Failure
import zhttp.http.{Request, Response}
import zio._

trait ReadingHandler {
  def fetchAll: IO[Failure, Response]

  def fetch(readingId: String): IO[Failure, Response]

  def create(request: Request): IO[Failure, Response]

  def update(readingId: String)(request: Request): IO[Failure, Response]

  def delete(readingId: String): IO[Failure, Response]
}

object ReadingHandler {
  val fetchAll: ZIO[ReadingHandler, Failure, Response] = ZIO.serviceWithZIO(_.fetchAll)

  def fetch(readingId: String): ZIO[ReadingHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.fetch(readingId))

  def create(request: Request): ZIO[ReadingHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.create(request))

  def update(readingId: String)(request: Request): ZIO[ReadingHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.update(readingId)(request))

  def delete(readingId: String): ZIO[ReadingHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.delete(readingId))
}
