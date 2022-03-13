package booklet.services.book_handler

import booklet.Failure
import zhttp.http.{Request, Response}
import zio.{Has, IO, RIO, UIO, ZIO}

trait BookHandler {
  def fetchAll: UIO[Response]

  def fetch(bookId: String): UIO[Response]

  def create(request: Request): IO[Failure, Response]

  def update(bookId: String)(request: Request): IO[Failure, Response]

  def delete(bookId: String): UIO[Response]
}

object BookHandler {
  val fetchAll: RIO[Has[BookHandler], Response] = RIO.serviceWith(_.fetchAll)

  def fetch(bookId: String): RIO[Has[BookHandler], Response] = RIO.serviceWith(_.fetch(bookId))

  def create(request: Request): ZIO[Has[BookHandler], Failure, Response] =
    ZIO.serviceWith(_.create(request))

  def update(bookId: String)(request: Request): ZIO[Has[BookHandler], Failure, Response] =
    ZIO.serviceWith(_.update(bookId)(request))

  def delete(bookId: String): RIO[Has[BookHandler], Response] = RIO.serviceWith(_.delete(bookId))
}
