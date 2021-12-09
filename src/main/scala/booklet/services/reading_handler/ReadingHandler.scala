package booklet.services.reading_handler

import booklet.Failure
import zhttp.http.{Request, UResponse}
import zio.{Has, IO, RIO, UIO, ZIO}

trait ReadingHandler {
  def fetchAll: UIO[UResponse]

  def fetch(readingId: String): UIO[UResponse]

  def create(request: Request): IO[Failure, UResponse]

  def update(readingId: String)(request: Request): IO[Failure, UResponse]

  def delete(readingId: String): UIO[UResponse]
}

object ReadingHandler {
  val fetchAll: RIO[Has[ReadingHandler], UResponse] = RIO.serviceWith(_.fetchAll)

  def fetch(readingId: String): RIO[Has[ReadingHandler], UResponse] =
    RIO.serviceWith(_.fetch(readingId))

  def create(request: Request): ZIO[Has[ReadingHandler], Failure, UResponse] =
    ZIO.serviceWith(_.create(request))

  def update(readingId: String)(request: Request): ZIO[Has[ReadingHandler], Failure, UResponse] =
    ZIO.serviceWith(_.update(readingId)(request))

  def delete(readingId: String): RIO[Has[ReadingHandler], UResponse] =
    RIO.serviceWith(_.delete(readingId))
}
