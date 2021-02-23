package services.book_finder

import model.Book
import zio.ZIO

object BookFinder {
  trait Service {
    def findByIsbn(isbn: String): ZIO[Any, Throwable, Option[Book]]
  }

  def findByIsbn(isbn: String): ZIO[BookFinder, Throwable, Option[Book]] =
    ZIO.accessM(r => r.get.findByIsbn(isbn))
}
