package http

import zhttp.http._
import zhttp.service.Server
import zio._

object HelloWorld extends zio.App {
  val app = Http.collect[Request] { case Method.GET -> Root / "text" =>
    Response.text("Hello World!")
  }

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    Server.start(port = 8090, app).exitCode
}
