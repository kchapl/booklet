package booklet.impure

import booklet.impure.service._
import booklet.impure.service.bookfinder.{GoogleBookFinder, GoogleBookFinderLive}
import booklet.impure.service.database.DatabaseLive
import booklet.pure.http
import booklet.pure.http.CustomResponse.{badRequest, serverFailure}
import booklet.pure.http.Query
import booklet.pure.views.{RootView, SignInView}
import zhttp.http.Method.{DELETE, GET, PATCH, POST}
import zhttp.http._
import zhttp.service.{ChannelFactory, EventLoopGroup, Server}
import zio._

object Router extends ZIOAppDefault {

  private val rootApp = Http.collect[Request] {
    case GET -> !!             => http.CustomResponse.ok(data = RootView.show.toString)
    case GET -> !! / "sign-in" => http.CustomResponse.ok(data = SignInView.show.toString)
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
        case GET -> !! / "books"                  => BookHandler.fetchAll
        case GET -> !! / "books" / bookId         => BookHandler.fetch(bookId)
        case req @ POST -> !! / "books"           => BookHandler.create(req)
        case req @ PATCH -> !! / "books" / bookId => BookHandler.update(bookId)(req)
        case DELETE -> !! / "books" / bookId      => BookHandler.delete(bookId)
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
