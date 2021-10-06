package booklet

import booklet.http.CustomResponse.seeOther
import booklet.services.book_handler.{BookHandler, BookHandlerLive}
import booklet.services.configuration.{Configuration, ConfigurationLive}
import booklet.services.database.Database
import zhttp.http.Method.{GET, PATCH, POST}
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
      case GET -> Root / "books"                    => BookHandler.fetchAll
      case GET -> Root / "books" / bookId           => BookHandler.fetch(bookId)
      case req @ POST -> Root / "books"             => BookHandler.create(req)
      case req @ PATCH -> Root / "books" / bookId   => BookHandler.update(bookId)(req)
      case Method.DELETE -> Root / "books" / bookId => BookHandler.delete(bookId)
    }

  private val program =
    Configuration.get.toManaged_.flatMap { config =>
      (Server.port(config.app.port) ++ Server.app(rootApp +++ bookApp)).make
        .mapError(Failure(_))
        .use(_ =>
          console
            .putStrLn(s"Server started on port ${config.app.port}")
            .mapError(Failure(_)) *> ZIO.never
        )
        .toManaged_
    }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program
      .provideCustomLayer(
        ConfigurationLive.layer ++
          (ConfigurationLive.layer >>> Database.live >>> BookHandlerLive.layer) ++
          ServerChannelFactory.auto ++
          EventLoopGroup.auto(nThreads = 1)
      )
      .useForever
      .exitCode
}
