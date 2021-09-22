package booklet

import booklet.http.CustomResponse.toContent
import booklet.http.{CustomResponse, Query}
import booklet.model.{BookData, BookId}
import booklet.services.book_handler.BookHandler
import booklet.services.configuration.Configuration
import booklet.services.database.Database
import booklet.views.BookView
import io.netty.handler.codec.http.HttpHeaderNames.LOCATION
import zhttp.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, SEE_OTHER}
import zhttp.http._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._

object Router extends zio.App {

  private val rootApp = Http.collect[Request] { case Method.GET -> Root =>
    Response.http(
      status = SEE_OTHER,
      headers = List(Header(LOCATION, "/books"))
    )
  }

  private val bookApp2: Http[Has[BookHandler], Throwable, Request, UResponse] =
    Http.collectM[Request] {
      case Method.GET -> Root / "books"          => BookHandler.fetchAll
      case Method.GET -> Root / "books" / bookId => BookHandler.fetch(bookId)
    }

  private val bookApp: Http[Has[Database], Throwable, Request, UResponse] =
    Http.collectM[Request] {

      case Method.GET -> Root / "books" / bookId =>
        ZIO
          .fromOption(bookId.toLongOption)
          .map(BookId)
          .foldM(
            _ =>
              ZIO.succeed(
                Response.http(
                  status = BAD_REQUEST,
                  content = toContent(s"Cannot parse ID $bookId")
                )
              ),
            id =>
              Database
                .fetchBook(id)
                .fold(
                  failure =>
                    Response.http(
                      status = INTERNAL_SERVER_ERROR,
                      content = toContent(failure.message)
                    ),
                  {
                    case None =>
                      Response.http(
                        status = NOT_FOUND,
                        content = toContent(s"No such book: $bookId")
                      )
                    case Some(book) => CustomResponse.htmlString(BookView.list(Seq(book)).toString)
                  }
                )
          )

      case req @ Method.POST -> Root / "books" =>
        val requestQry = Query.fromRequest(req)
        ZIO
          .fromOption(BookData.completeFromHttpQuery(requestQry))
          .foldM(
            _ =>
              ZIO.succeed(
                Response.http(
                  status = BAD_REQUEST,
                  content = toContent(requestQry.toString)
                )
              ),
            bookData =>
              Database
                .insertBook(bookData)
                .fold(
                  failure =>
                    Response.http(
                      status = INTERNAL_SERVER_ERROR,
                      content = toContent(failure.message)
                    ),
                  _ =>
                    Response.http(
                      status = SEE_OTHER,
                      headers = List(Header(LOCATION, "/books"))
                    )
                )
          )

      case req @ Method.PATCH -> Root / "books" / bookId =>
        val requestQry = Query.fromRequest(req)
        ZIO
          .fromOption(bookId.toLongOption)
          .map(BookId)
          .foldM(
            _ =>
              ZIO.succeed(
                Response.http(
                  status = BAD_REQUEST,
                  content = toContent(requestQry.toString)
                )
              ),
            id =>
              Database
                .updateBook(id, BookData.partialFromHttpQuery(requestQry))
                .fold(
                  failure =>
                    Response.http(
                      status = INTERNAL_SERVER_ERROR,
                      content = toContent(failure.message)
                    ),
                  _ =>
                    Response.http(
                      status = SEE_OTHER,
                      headers = List(Header(LOCATION, "/books"))
                    )
                )
          )

      case Method.DELETE -> Root / "books" / bookId =>
        ZIO
          .fromOption(bookId.toLongOption)
          .map(BookId)
          .foldM(
            _ =>
              ZIO.succeed(
                Response.http(
                  status = BAD_REQUEST,
                  content = toContent(bookId)
                )
              ),
            id =>
              Database
                .deleteBook(id)
                .fold(
                  failure =>
                    Response.http(
                      status = INTERNAL_SERVER_ERROR,
                      content = toContent(failure.message)
                    ),
                  _ =>
                    Response.http(
                      status = SEE_OTHER,
                      headers = List(Header(LOCATION, "/books"))
                    )
                )
          )
    }

  val program = {
    for {
      config <- Configuration.load.toManaged_
      server <- (Server.port(config.app.port) ++
        Server.app(rootApp +++ bookApp2)).make
        .mapError(Failure(_))
        .use(_ =>
          console
            .putStrLn(s"Server started on port ${config.app.port}")
            .mapError(Failure(_)) *> ZIO.never
        )
        .toManaged_
    } yield server
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program
      .provideCustomLayer(
        Configuration.live ++
          (Configuration.live >>> Database.live >>> BookHandler.live) ++
          ServerChannelFactory.auto ++
          EventLoopGroup.auto(nThreads = 1)
      )
      .useForever
      .exitCode
}
