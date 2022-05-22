package booklet.services.book_finder

import booklet.Failure
import booklet.model.BookData
import booklet.services.book_finder.Model.GoogleBookResult
import booklet.services.book_finder.Model.GoogleBookResult.toBook
import booklet.utility.OptionPickler.read
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio._

trait BookFinder {
  def findByIsbn(isbn: String): IO[Failure, Option[BookData]]
}

object BookFinder {
  def findByIsbn(isbn: String): ZIO[BookFinder, Failure, Option[BookData]] =
    ZIO.serviceWithZIO(_.findByIsbn(isbn))
}

object BookFinderLive {

  val layer: ZLayer[EventLoopGroup with ChannelFactory, Failure, BookFinder] = ZLayer.fromZIO(for {
    eventLoopGroup <- ZIO.service[EventLoopGroup]
    channelFactory <- ZIO.service[ChannelFactory]
  } yield new BookFinder {
    override def findByIsbn(isbn: String): IO[Failure, Option[BookData]] =
      for {
        response <- Client
          .request(s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn")
          .provide(ZLayer.succeed(eventLoopGroup), ZLayer.succeed(channelFactory))
          .mapError(Failure.fromThrowable)
        responseBody <- response.bodyAsString.mapError(Failure.fromThrowable)
        book <- ZIO.attempt(read[GoogleBookResult](responseBody)).mapError(Failure.fromThrowable)
      } yield toBook(book)
  })
}
