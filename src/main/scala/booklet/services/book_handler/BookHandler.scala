package booklet.services.book_handler

import booklet.Failure
import zhttp.http.{Request, UResponse}
import zio.{Has, IO, RIO, UIO, ZIO}

trait BookHandler {
  def fetchAll: UIO[UResponse]

  def fetch(bookId: String): UIO[UResponse]

  def create(request: Request): IO[Failure, UResponse]

  def update(bookId: String)(request: Request): IO[Failure, UResponse]

  def delete(bookId: String): UIO[UResponse]
}

object BookHandler {
  val fetchAll: RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.fetchAll)

  def fetch(bookId: String): RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.fetch(bookId))

  def create(request: Request): ZIO[Has[BookHandler], Failure, UResponse] =
    ZIO.serviceWith(_.create(request))

  def update(bookId: String)(request: Request): ZIO[Has[BookHandler], Failure, UResponse] =
    ZIO.serviceWith(_.update(bookId)(request))

  def delete(bookId: String): RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.delete(bookId))
}
