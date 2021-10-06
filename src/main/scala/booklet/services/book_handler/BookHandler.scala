package booklet.services.book_handler

import zhttp.http.{Request, UResponse}
import zio.{Has, RIO, UIO}

trait BookHandler {
  def fetchAll: UIO[UResponse]
  def fetch(bookId: String): UIO[UResponse]
  def create(request: Request): UIO[UResponse]
  def update(bookId: String)(request: Request): UIO[UResponse]
  def delete(bookId: String): UIO[UResponse]
}

object BookHandler {
  val fetchAll: RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.fetchAll)
  def fetch(bookId: String): RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.fetch(bookId))
  def create(request: Request): RIO[Has[BookHandler], UResponse] =
    RIO.serviceWith(_.create(request))
  def update(bookId: String)(request: Request): RIO[Has[BookHandler], UResponse] =
    RIO.serviceWith(_.update(bookId)(request))
  def delete(bookId: String): RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.delete(bookId))
}
