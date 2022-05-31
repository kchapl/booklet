package booklet.service

import booklet.Failure
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio.{IO, ZIO, ZLayer}

trait GoogleBookFinder {
  def findByIsbn(isbn: String): IO[Failure, String]
}

object GoogleBookFinder {
  def findByIsbn(isbn: String): ZIO[GoogleBookFinder, Failure, String] =
    ZIO.serviceWithZIO(_.findByIsbn(isbn))
}

object GoogleBookFinderLive {

  val layer: ZLayer[EventLoopGroup with ChannelFactory, Failure, GoogleBookFinder] =
    ZLayer.fromZIO(for {
      eventLoopGroup <- ZIO.service[EventLoopGroup]
      channelFactory <- ZIO.service[ChannelFactory]
    } yield new GoogleBookFinder {

      override def findByIsbn(isbn: String): IO[Failure, String] =
        findByIsbnInternal(isbn.replaceAll("[^\\d]", ""))

      private def findByIsbnInternal(isbn: String): IO[Failure, String] =
        for {
          response <- Client
            .request(s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn")
            .provide(ZLayer.succeed(eventLoopGroup), ZLayer.succeed(channelFactory))
            .mapError(Failure.fromThrowable)
          responseBody <- response.bodyAsString.mapError(Failure.fromThrowable).debug("book")
        } yield responseBody
    })
}
