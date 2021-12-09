package booklet

import booklet.http.CustomResponse.{badRequest, notFound, serverFailure}
import booklet.http.Query
import booklet.services.book_finder.{BookFinder, BookFinderLive}
import booklet.services.book_handler.{BookHandler, BookHandlerLive}
import booklet.services.database.DatabaseLive
import booklet.services.reading_handler.{ReadingHandler, ReadingHandlerLive}
import booklet.services.{StaticFile, StaticFileLive}
import booklet.views.RootView
import zhttp.http.Method.{DELETE, GET, PATCH, POST}
import zhttp.http._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._

object Router extends zio.App {

  private val rootApp = Http.collect[Request] { case GET -> !! =>
    http.CustomResponse.ok(data = RootView.show.toString)
  }

  private val staticApp: Http[Has[StaticFile], Throwable, Request, UResponse] =
    Http.collectM[Request] { case GET -> !! / "javascript" / script =>
      val y: RIO[Has[StaticFile], UResponse] = for {
        x <- StaticFile.fetchContent(path = s"public/javascript/$script")
      } yield http.CustomResponse.okJs(data = x)
      y
    }

  private val bookApp: Http[Has[BookHandler], Throwable, Request, UResponse] =
    Http.collectM[Request] {
      case GET -> !! / "books"          => BookHandler.fetchAll
      case GET -> !! / "books" / bookId => BookHandler.fetch(bookId)
      case req @ POST -> !! / "books" =>
        BookHandler.create(req).mapError(failure => new RuntimeException(failure.message))
      case req @ PATCH -> !! / "books" / bookId =>
        BookHandler.update(bookId)(req).mapError(failure => new RuntimeException(failure.message))
      case DELETE -> !! / "books" / bookId => BookHandler.delete(bookId)
    }

  private val readingApp: Http[Has[ReadingHandler], Throwable, Request, UResponse] =
    Http.collectM[Request] {
      case GET -> !! / "readings"             => ReadingHandler.fetchAll
      case GET -> !! / "readings" / readingId => ReadingHandler.fetch(readingId)
      case req @ POST -> !! / "readings" =>
        ReadingHandler.create(req).mapError(failure => new RuntimeException(failure.message))
      case req @ PATCH -> !! / "readings" / readingId =>
        ReadingHandler
          .update(readingId)(req)
          .mapError(failure => new RuntimeException(failure.message))
      case DELETE -> !! / "readings" / readingId => ReadingHandler.delete(readingId)
    }

  private val bookFinderApp: Http[Has[BookFinder], Throwable, Request, UResponse] =
    Http.collectM[Request] { case req @ GET -> !! / "find" =>
      Query.param(req)(name = "isbn") match {
        case None => ZIO.succeed(badRequest("Missing ISBN"))
        case Some(isbn) =>
          BookFinder
            .findByIsbn(isbn)
            .fold(
              serverFailure,
              {
                case None       => notFound(Path(isbn))
                case Some(book) => Response.text(book.toString)
              }
            )
      }
    }

  private val program =
    ZIO.service[Config].toManaged_.flatMap { config =>
      (Server.port(config.app.port) ++ Server.app(
        rootApp ++ staticApp ++ bookApp ++ readingApp ++ bookFinderApp
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
          StaticFileLive.layer ++
          (Config.load.toLayer >>> DatabaseLive.layer >>> BookHandlerLive.layer) ++
          (Config.load.toLayer >>> DatabaseLive.layer >>> ReadingHandlerLive.layer) ++
          BookFinderLive.layer ++
          ServerChannelFactory.auto ++
          EventLoopGroup.auto(nThreads = 1)
      )
      .useForever
      .exitCode
}
