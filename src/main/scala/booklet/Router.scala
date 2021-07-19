package booklet

import booklet.Config.config
import booklet.http.CustomResponse.toContent
import booklet.http.{CustomResponse, Query}
import booklet.model.BookData
import booklet.services.database.Database
import booklet.services.database.Database.Database
import booklet.views.BookView
import io.netty.handler.codec.http.HttpHeaderNames.LOCATION
import zhttp.http.HttpData.CompleteData
import zhttp.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, SEE_OTHER}
import zhttp.http._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._

object Router extends zio.App {

  private val app = Http.collect[Request] { case Method.GET -> Root / "text" =>
    Response.text("Hello World!")
  }

  private val app2: Http[Database, Throwable, Request, UResponse] = Http.collectM[Request] {

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

    case req @ Method.POST -> Root / "books" / "add" =>
      val requestQry = req.content match {
        case CompleteData(data) => Query.fromQueryString(new String(data.toArray, HTTP_CHARSET))
        case _                  => Map.empty[String, String]
      }
      ZIO
        .fromOption(BookData.fromHttpQuery(requestQry))
        .foldM(
          _ =>
            ZIO.succeed(
              Response.http(status = BAD_REQUEST, content = toContent(requestQry.toString))
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
  }

  val program =
    (Server.port(config.port) ++
      Server.app(app +++ app2)).make
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
