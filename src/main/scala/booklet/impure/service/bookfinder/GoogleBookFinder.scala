package booklet.impure.service.bookfinder

import booklet.impure.service.bookfinder.GoogleBookModel.GoogleBookResult.toBook
import booklet.impure.service.bookfinder.GoogleBookModel.{EmptyGoogleBookResult, GoogleBookResult}
import booklet.pure.Failure
import booklet.pure.model.BookData
import booklet.pure.utility.OptionPickler._
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio._

trait GoogleBookFinder {
  def findByIsbn(isbn: String): IO[Failure, Option[BookData]]
}

object GoogleBookFinder {
  def findByIsbn(isbn: String): ZIO[GoogleBookFinder, Failure, Option[BookData]] =
    ZIO.serviceWithZIO(_.findByIsbn(isbn))
}

object GoogleBookFinderLive {

  val layer: ZLayer[EventLoopGroup with ChannelFactory, Failure, GoogleBookFinder] =
    ZLayer.fromZIO(for {
      eventLoopGroup <- ZIO.service[EventLoopGroup]
      channelFactory <- ZIO.service[ChannelFactory]
    } yield new GoogleBookFinder {

      override def findByIsbn(isbn: String): IO[Failure, Option[BookData]] =
        findByIsbnInternal(isbn.replaceAll("\\D", ""))

      private def findByIsbnInternal(isbn: String): IO[Failure, Option[BookData]] =
        for {
          response <- Client
            .request(s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn")
            .provide(ZLayer.succeed(eventLoopGroup), ZLayer.succeed(channelFactory))
            .mapError(Failure.fromThrowable)
          responseBody <- response.bodyAsString.mapError(Failure.fromThrowable)
          book <- ZIO
            .attempt(read[GoogleBookResult](responseBody))
            .map(toBook)
            .orElse(ZIO.attempt(read[EmptyGoogleBookResult](responseBody)).as(None))
            .mapError(Failure.fromThrowable)
            .debug("result")
        } yield book

    })
}
