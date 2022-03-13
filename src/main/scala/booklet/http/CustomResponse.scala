package booklet.http

import booklet.Failure
import io.netty.handler.codec.http.HttpHeaderNames.{CONTENT_TYPE, LOCATION}
import io.netty.handler.codec.http.HttpHeaderValues.TEXT_HTML
import io.netty.util.AsciiString
import zhttp.http.Status.SEE_OTHER
import zhttp.http._

object CustomResponse {

  private def toData(text: String): HttpData =
    HttpData.fromString(text, HTTP_CHARSET)

  private def ok(data: String, contentType: AsciiString): Response =
    Response(
      headers = Headers(CONTENT_TYPE -> contentType),
      data = toData(data)
    )

  def ok(data: String): Response =
    ok(data, TEXT_HTML)

  def okJs(data: String): Response =
    ok(data, AsciiString.cached("text/javascript"))

  def seeOther(path: String): Response =
    Response(
      status = SEE_OTHER,
      headers = Headers(LOCATION -> path)
    )

  def badRequest(message: String): Response =
    Response.fromHttpError(HttpError.BadRequest(message))

  def notFound(path: Path): Response =
    Response.fromHttpError(HttpError.NotFound(path))

  def serverFailure(failure: Failure): Response =
    Response.fromHttpError(HttpError.InternalServerError(failure.message, failure.cause))
}
