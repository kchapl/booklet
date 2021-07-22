package booklet

import booklet.Config.config
import booklet.http.CustomResponse.toContent
import booklet.http.{CustomResponse, Query}
import booklet.model.{BookData, Id}
import booklet.services.database.Database
import booklet.services.database.Database.Database
import booklet.views.BookView
import io.netty.handler.codec.http.HttpHeaderNames.LOCATION
import zhttp.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, SEE_OTHER}
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

  private val bookApp: Http[Database, Throwable, Request, UResponse] = Http.collectM[Request] {

    case Method.GET -> Root / "books" =>
      Database
        .fetchAllBooks()
        .fold(
          failure =>
            Response.http(
              status = INTERNAL_SERVER_ERROR,
              content = toContent(failure.message)
            ),
          books => CustomResponse.htmlString(BookView.list(books).toString)
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
        .map(Id)
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
        .map(Id)
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

  val program =
    (Server.port(config.port) ++
      Server.app(rootApp +++ bookApp)).make
      .mapError(Failure(_))
      .use(_ =>
        console.putStrLn(s"Server started on port ${config.port}").mapError(Failure(_)) *> ZIO.never
      )

  val layer = ServerChannelFactory.auto ++ EventLoopGroup.auto(nThreads = 1) ++ Database.live

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program
      .provideCustomLayer(layer)
      .exitCode
}
