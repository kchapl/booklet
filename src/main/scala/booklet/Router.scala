package booklet

import booklet.http.CustomResponse.{badRequest, serverFailure}
import booklet.http.Query
import booklet.service.bookfinder.{GoogleBookFinder, GoogleBookFinderLive}
import booklet.services.book_handler.{BookHandler, BookHandlerLive}
import booklet.services.database.DatabaseLive
import booklet.services.reading_handler.{ReadingHandler, ReadingHandlerLive}
import booklet.services.{StaticFile, StaticFileLive}
import booklet.views.RootView
import zhttp.http.Method.{DELETE, GET, PATCH, POST}
import zhttp.http._
import zhttp.service.{ChannelFactory, EventLoopGroup, Server}
import zio._

object Router extends ZIOAppDefault {

  private val books = "books"

  private val rootApp = Http.collect[Request] { case GET -> !! =>
    http.CustomResponse.ok(data = RootView.show.toString)
  }

  private val staticApp =
    Http.collectZIO[Request] { case GET -> !! / "javascript" / script =>
      for {
        content <- StaticFile.fetchContent(path = s"public/javascript/$script")
      } yield http.CustomResponse.okJs(data = content)
    }

  private val bookApp =
    Http
      .collectZIO[Request] {
        case GET -> !! / `books`                  => BookHandler.fetchAll
        case GET -> !! / `books` / bookId         => BookHandler.fetch(bookId)
        case req @ POST -> !! / `books`           => BookHandler.create(req)
        case req @ PATCH -> !! / `books` / bookId => BookHandler.update(bookId)(req)
        case DELETE -> !! / `books` / bookId      => BookHandler.delete(bookId)
      }
      .mapError(failure => new RuntimeException(failure.message))

  private val readingApp =
    Http
      .collectZIO[Request] {
        case GET -> !! / "readings"                     => ReadingHandler.fetchAll
        case GET -> !! / "readings" / readingId         => ReadingHandler.fetch(readingId)
        case req @ POST -> !! / "readings"              => ReadingHandler.create(req)
        case req @ PATCH -> !! / "readings" / readingId => ReadingHandler.update(readingId)(req)
        case DELETE -> !! / "readings" / readingId      => ReadingHandler.delete(readingId)
      }
      .mapError(failure => new RuntimeException(failure.message))

  private val bookFinderApp =
    Http.collectZIO[Request] { case req @ GET -> !! / "find" =>
      Query.param(req)(name = "isbn") match {
        case None => ZIO.succeed(badRequest("Missing ISBN"))
        case Some(isbn) =>
          GoogleBookFinder
            .findByIsbn(isbn)
            .fold(
              serverFailure,
              {
                case None       => badRequest(s"No book found for ISBN $isbn")
                case Some(book) => Response.text(book.toString)
              }
            )
      }
    }

  private val program =
    for {
      config <- Config.service
      _ <- Server.start(
        port = config.app.port,
        http = rootApp ++ staticApp ++ bookApp ++ readingApp ++ bookFinderApp
      )
    } yield ()

  override def run: ZIO[ZIOAppArgs, Any, Any] =
    program
      .provide(
        ConfigLive.layer,
        DatabaseLive.layer,
        BookHandlerLive.layer,
        ReadingHandlerLive.layer,
        StaticFileLive.layer,
        GoogleBookFinderLive.layer,
        EventLoopGroup.auto(),
        ChannelFactory.auto
      )
      .forever
}
