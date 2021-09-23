package booklet.services.book_handler

import zhttp.http.UResponse
import zio.{Has, RIO, UIO}

trait BookHandler {
  def fetchAll: UIO[UResponse]

  def fetch(bookId: String): UIO[UResponse]
}

object BookHandler {
  val fetchAll: RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.fetchAll)

  def fetch(bookId: String): RIO[Has[BookHandler], UResponse] = RIO.serviceWith(_.fetch(bookId))
}
