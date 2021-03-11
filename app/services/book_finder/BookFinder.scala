package services.book_finder

import model.{BookLookupFailure, BookToInsert}
import zio.ZIO

object BookFinder {
  trait Service {
    def findByIsbn(isbn: String): ZIO[Any, BookLookupFailure, Option[BookToInsert]]
  }

  def findByIsbn(isbn: String): ZIO[BookFinder, BookLookupFailure, Option[BookToInsert]] =
    ZIO.accessM(r => r.get.findByIsbn(isbn))
}
