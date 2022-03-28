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
import zhttp.service.Server
import zio._

object Router extends ZIOAppDefault {

  private val rootApp = Http.collect[Request] { case GET -> !! =>
    http.CustomResponse.ok(data = RootView.show.toString)
  }

  private val staticApp: Http[StaticFile, Throwable, Request, Response] =
    Http.collectZIO[Request] { case GET -> !! / "javascript" / script =>
      for {
        content <- StaticFile.fetchContent(path = s"public/javascript/$script")
      } yield http.CustomResponse.okJs(data = content)
    }

  private val bookApp: Http[BookHandler, Throwable, Request, Response] =
    Http
      .collectZIO[Request] {
        case GET -> !! / "books"                  => BookHandler.fetchAll
        case GET -> !! / "books" / bookId         => BookHandler.fetch(bookId)
        case req @ POST -> !! / "books"           => BookHandler.create(req)
        case req @ PATCH -> !! / "books" / bookId => BookHandler.update(bookId)(req)
        case DELETE -> !! / "books" / bookId      => BookHandler.delete(bookId)
      }
      .mapError(failure => new RuntimeException(failure.message))

  private val readingApp: Http[ReadingHandler, Throwable, Request, Response] =
    Http
      .collectZIO[Request] {
        case GET -> !! / "readings"                     => ReadingHandler.fetchAll
        case GET -> !! / "readings" / readingId         => ReadingHandler.fetch(readingId)
        case req @ POST -> !! / "readings"              => ReadingHandler.create(req)
        case req @ PATCH -> !! / "readings" / readingId => ReadingHandler.update(readingId)(req)
        case DELETE -> !! / "readings" / readingId      => ReadingHandler.delete(readingId)
      }
      .mapError(failure => new RuntimeException(failure.message))

  private val bookFinderApp: Http[BookFinder, Throwable, Request, Response] =
    Http.collectZIO[Request] { case req @ GET -> !! / "find" =>
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

  override def run: ZIO[ZEnv with ZIOAppArgs, Any, Any] = {
    val loadedConfig = Config.load
    for {
      config <- loadedConfig
      _ <- Server
        .start(config.app.port, rootApp ++ staticApp ++ bookApp ++ readingApp ++ bookFinderApp)
        .provide(
          StaticFileLive.layer,
          BookHandlerLive.layer,
          ReadingHandlerLive.layer,
          BookFinderLive.layer,
          DatabaseLive.layer,
          loadedConfig.toLayer
        )
        .exitCode
    } yield ()
  }
}
