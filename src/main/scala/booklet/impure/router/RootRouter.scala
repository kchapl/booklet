package booklet.impure.router

import booklet.pure.http
import booklet.pure.views.RootView
import zhttp.http.Method.{GET, POST}
import zhttp.http._
import zio.json._

object RootRouter {
  val app: Http[Any, Nothing, Request, Response] = Http.collect[Request] {
    case GET -> !! =>
      http.CustomResponse.ok(body = Body.fromString(RootView.show.toString))
    case req @ POST -> !! / "api" / "authenticate" =>
      for {
        body <- req.bodyAsString
        data <- ZIO.fromEither(body.fromJson[Map[String, String]]).orElseFail(http.CustomResponse.badRequest("Invalid JSON"))
        idToken = data.getOrElse("id_token", "")
        userId = data.getOrElse("user_id", "")
        // Handle the ID token and user ID (e.g., store in session, validate, etc.)
      } yield http.CustomResponse.ok()
  }
}
