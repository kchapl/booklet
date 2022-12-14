package booklet.impure.router

import booklet.pure.http
import booklet.pure.views.RootView
import zhttp.http.Method.GET
import zhttp.http._

object RootRouter {
  val app: Http[Any, Nothing, Request, Response] = Http.collect[Request] { case GET -> !! =>
    http.CustomResponse.ok(body = Body.fromString(RootView.show.toString))
  }
}
