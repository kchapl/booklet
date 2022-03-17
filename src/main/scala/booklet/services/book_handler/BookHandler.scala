package booklet.services.book_handler

import booklet.Failure
import zhttp.http.{Request, Response}
import zio.{IO, ZIO}

trait BookHandler {
  def fetchAll: IO[Failure, Response]

  def fetch(bookId: String): IO[Failure, Response]

  def create(request: Request): IO[Failure, Response]

  def update(bookId: String)(request: Request): IO[Failure, Response]

  def delete(bookId: String): IO[Failure, Response]
}

object BookHandler {
  val fetchAll: ZIO[BookHandler, Failure, Response] = ZIO.serviceWithZIO(_.fetchAll)

  def fetch(bookId: String): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.fetch(bookId))

  def create(request: Request): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.create(request))

  def update(bookId: String)(request: Request): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.update(bookId)(request))

  def delete(bookId: String): ZIO[BookHandler, Failure, Response] =
    ZIO.serviceWithZIO(_.delete(bookId))
}
