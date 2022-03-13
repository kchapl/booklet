package booklet.services.reading_handler

import booklet.Failure
import zhttp.http.{Request, Response}
import zio.{Has, IO, RIO, UIO, ZIO}

trait ReadingHandler {
  def fetchAll: UIO[Response]

  def fetch(readingId: String): UIO[Response]

  def create(request: Request): IO[Failure, Response]

  def update(readingId: String)(request: Request): IO[Failure, Response]

  def delete(readingId: String): UIO[Response]
}

object ReadingHandler {
  val fetchAll: RIO[Has[ReadingHandler], Response] = RIO.serviceWith(_.fetchAll)

  def fetch(readingId: String): RIO[Has[ReadingHandler], Response] =
    RIO.serviceWith(_.fetch(readingId))

  def create(request: Request): ZIO[Has[ReadingHandler], Failure, Response] =
    ZIO.serviceWith(_.create(request))

  def update(readingId: String)(request: Request): ZIO[Has[ReadingHandler], Failure, Response] =
    ZIO.serviceWith(_.update(readingId)(request))

  def delete(readingId: String): RIO[Has[ReadingHandler], Response] =
    RIO.serviceWith(_.delete(readingId))
}
