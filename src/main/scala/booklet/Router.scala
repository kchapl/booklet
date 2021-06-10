package booklet

import booklet.Config.config
import booklet.http.CustomResponse
import booklet.services.Database
import booklet.views.BookView
import zhttp.http._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._

object Router extends zio.App {

//  private val app = Http.collect[Request] { case Method.GET -> Root / "text" =>
//    Response.text("Hello World!")
//  }

  private val app2 = Http.collectM[Request] { case Method.GET -> Root / "books" =>
    Database.fetchAllBooks().map { books =>
      CustomResponse.htmlString(BookView.list(books).toString)
    }
  }

  private def server(port: Int) =
    Server.port(port) ++
//      Server.app(app +++ app2)
      Server.app(app2)

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    server(config.port).make
      .use(_ => console.putStrLn(s"Server started on port ${config.port}") *> ZIO.never)
      .provideCustomLayer(
        ServerChannelFactory.auto ++ EventLoopGroup.auto(nThreads = 1) ++ Database.live
      )
      .exitCode
}
