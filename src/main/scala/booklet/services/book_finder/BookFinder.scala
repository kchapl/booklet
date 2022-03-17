package booklet.services.book_finder

import booklet.Failure
import booklet.model.BookData
import zio._

trait BookFinder {
  def findByIsbn(isbn: String): IO[Failure, Option[BookData]]
}

object BookFinder {
  def findByIsbn(isbn: String): ZIO[BookFinder, Failure, Option[BookData]] =
    ZIO.serviceWithZIO(_.findByIsbn(isbn))
}
