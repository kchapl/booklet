package services.book_finder

import model.BookToInsert
import zio.ZIO

object BookFinder {
  trait Service {
    def findByIsbn(isbn: String): ZIO[Any, Throwable, Option[BookToInsert]]
  }

  def findByIsbn(isbn: String): ZIO[BookFinder, Throwable, Option[BookToInsert]] =
    ZIO.accessM(r => r.get.findByIsbn(isbn))
}
