package booklet.services.book_finder

import booklet.Failure
import booklet.model.BookData
import booklet.service.GoogleBookFinder
import booklet.services.book_finder.Model.GoogleBookResult.toBook
import booklet.services.book_finder.Model.{EmptyGoogleBookResult, GoogleBookResult}
import booklet.utility.OptionPickler._
import zio._

trait BookFinder {
  def findByIsbn(isbn: String): IO[Failure, Option[BookData]]
}

object BookFinder {
  def findByIsbn(isbn: String): ZIO[BookFinder, Failure, Option[BookData]] =
    ZIO.serviceWithZIO(_.findByIsbn(isbn))
}

object BookFinderLive {

  val layer: ZLayer[GoogleBookFinder, Failure, BookFinder] = ZLayer.fromZIO(for {
    googleBookFinder <- ZIO.service[GoogleBookFinder]
  } yield new BookFinder {
    override def findByIsbn(isbn: String): IO[Failure, Option[BookData]] =
      for {
        responseBody <- googleBookFinder.findByIsbn(isbn)
        book <- ZIO
          .attempt(read[GoogleBookResult](responseBody))
          .map(toBook)
          .debug("1")
          .orElse(ZIO.attempt(read[EmptyGoogleBookResult](responseBody)).as(None).debug("2"))
          .mapError(Failure.fromThrowable)
          .debug("result")
      } yield book
  })
}
