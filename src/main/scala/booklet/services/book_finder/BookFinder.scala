package booklet.services.book_finder

import booklet.Failure
import booklet.model.BookData
import zio.{Has, ZIO, ZLayer}

trait BookFinder {
  def findByIsbn(isbn: String): ZIO[Any, Failure, Option[BookData]]
}

object BookFinder {

  def findByIsbn(isbn: String): ZIO[Has[BookFinder], Failure, Option[BookData]] =
    ZIO.serviceWith(_.findByIsbn(isbn))

  val live: ZLayer[Any, Nothing, Has[BookFinder]] =
    ZLayer.succeed { _ =>
      ZIO.some(BookData(None, None, None, None, None, None))
    }
}
