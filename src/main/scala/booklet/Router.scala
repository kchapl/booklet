package booklet

import booklet.http.CustomResponse.{badRequest, notFound, seeOther, serverFailure}
import booklet.http.Query
import booklet.services.book_finder.{BookFinder, BookFinderLive}
import booklet.services.book_handler.{BookHandler, BookHandlerLive}
import booklet.services.database.DatabaseLive
import booklet.services.reading_handler.{ReadingHandler, ReadingHandlerLive}
import zhttp.http.Method.{DELETE, GET, PATCH, POST}
import zhttp.http._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._

object Router extends zio.App {

  private val rootApp = Http.collect[Request] { case GET -> Root =>
    seeOther(path = "/books")
  }

  private val bookApp: Http[Has[BookHandler], Throwable, Request, UResponse] =
    Http.collectM[Request] {
      case GET -> Root / "books"                  => BookHandler.fetchAll
      case GET -> Root / "books" / bookId         => BookHandler.fetch(bookId)
      case req @ POST -> Root / "books"           => BookHandler.create(req)
      case req @ PATCH -> Root / "books" / bookId => BookHandler.update(bookId)(req)
      case DELETE -> Root / "books" / bookId      => BookHandler.delete(bookId)
    }

  private val readingApp: Http[Has[ReadingHandler], Throwable, Request, UResponse] =
    Http.collectM[Request] {
      case GET -> Root / "readings"                     => ReadingHandler.fetchAll
      case GET -> Root / "readings" / readingId         => ReadingHandler.fetch(readingId)
      case req @ POST -> Root / "readings"              => ReadingHandler.create(req)
      case req @ PATCH -> Root / "readings" / readingId => ReadingHandler.update(readingId)(req)
      case DELETE -> Root / "readings" / readingId      => ReadingHandler.delete(readingId)
    }

  private val bookFinderApp: Http[Has[BookFinder], Throwable, Request, UResponse] =
    Http.collectM[Request] { case req @ GET -> Root / "find" =>
      Query.param(req)(name = "isbn") match {
        case None => ZIO.succeed(badRequest("Missing ISBN"))
        case Some(isbn) =>
          BookFinder
            .findByIsbn(isbn)
            .fold(
              serverFailure,
              {
                case None       => notFound(s"No book has ISBN $isbn")
                case Some(book) => Response.text(book.toString)
              }
            )
      }
    }

  private val program =
    ZIO.service[Config].toManaged_.flatMap { config =>
      (Server.port(config.app.port) ++ Server.app(
        rootApp +++ bookApp +++ readingApp +++ bookFinderApp
      )).make
        .mapError(Failure.fromThrowable)
        .use(_ =>
          console
            .putStrLn(s"Server started on port ${config.app.port}")
            .mapError(Failure.fromThrowable) *> ZIO.never
        )
        .toManaged_
    }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program
      .provideCustomLayer(
        Config.load.toLayer ++
          (Config.load.toLayer >>> DatabaseLive.layer >>> BookHandlerLive.layer) ++
          (Config.load.toLayer >>> DatabaseLive.layer >>> ReadingHandlerLive.layer) ++
          BookFinderLive.layer ++
          ServerChannelFactory.auto ++
          EventLoopGroup.auto(nThreads = 1)
      )
      .useForever
      .exitCode
}
