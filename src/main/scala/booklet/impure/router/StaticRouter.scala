package booklet.impure.router

import booklet.impure.service.StaticFile
import booklet.pure.http.CustomResponse.{notFound, okJs}
import zhttp.http.Method.GET
import zhttp.http._

object StaticRouter {
  val app: Http[StaticFile, Nothing, Request, Response] =
    Http.collectZIO[Request] { case GET -> !! / "javascript" / script =>
      StaticFile
        .fetchContent(path = s"public/javascript/$script")
        .fold(
          _ => notFound(!! / "javascript" / script),
          content => okJs(data = content)
        )
    }
}
