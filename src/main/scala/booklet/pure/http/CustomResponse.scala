package booklet.pure.http

import booklet.pure.Failure
import zhttp.http.Status.SeeOther
import zhttp.http._

object CustomResponse {

  def ok(body: Body, contentType: String): Response =
    Response(
      headers = Headers("content-type" -> contentType),
      body = body
    )

  def ok(body: Body): Response =
    ok(body, contentType = "text/html")

  def okJs(body: Body): Response =
    ok(body, contentType = "text/javascript")

  def seeOther(path: String): Response =
    Response(
      status = SeeOther,
      headers = Headers("location" -> path)
    )

  def badRequest(message: String): Response =
    Response.fromHttpError(HttpError.BadRequest(message))

  def notFound(path: Path): Response =
    Response.fromHttpError(HttpError.NotFound(path.toString))

  def serverFailure(failure: Failure): Response =
    Response.fromHttpError(HttpError.InternalServerError(failure.message, failure.cause))
}
