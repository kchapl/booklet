package booklet.impure.service.bookfinder

import booklet.impure.service.bookfinder.GoogleBookModel.GoogleBookResult.toBook
import booklet.impure.service.bookfinder.GoogleBookModel.{EmptyGoogleBookResult, GoogleBookResult}
import booklet.pure.Failure
import booklet.pure.model.BookData
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio._
import zio.json.DecoderOps

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
            .fromEither(responseBody.fromJson[GoogleBookResult])
            .map(toBook)
            .orElse(ZIO.fromEither(responseBody.fromJson[EmptyGoogleBookResult]).as(None))
            .mapError(Failure.fromDecodingException)
            .debug("result")
        } yield book

    })
}
