package http

import zhttp.http._
import zhttp.service.Server
import zio._

object HelloWorld extends zio.App {
  val app: Http[Any, Nothing, Request, UResponse] = Http.collect[Request] {
    case Method.GET -> Root / "text" =>
      Response.text("Hello World!")
  }

  val port: ZIO[system.System, Failure, Int] =
    for {
      optPortStr <- system.env("PORT").mapError(e => Failure(e.getMessage))
      portStr    <- ZIO.fromOption(optPortStr).orElseFail(Failure("Missing port"))
      port       <- ZIO.effect(portStr.toInt).mapError(e => Failure(s"Non-int port: ${e.getMessage}"))
    } yield port

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    (for {
      p <- port
      _ <- Server.start(port = p, app)
    } yield ()).exitCode
}
