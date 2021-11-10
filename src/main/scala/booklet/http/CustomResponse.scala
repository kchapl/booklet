package booklet.http

import booklet.Failure
import io.netty.handler.codec.http.HttpHeaderNames.{CONTENT_TYPE, LOCATION}
import io.netty.handler.codec.http.HttpHeaderValues.TEXT_HTML
import io.netty.util.AsciiString
import zhttp.http.HttpData.CompleteData
import zhttp.http.Status.SEE_OTHER
import zhttp.http._
import zio.Chunk

object CustomResponse {

  private def toContent(data: String): CompleteData =
    HttpData.CompleteData(Chunk.fromArray(data.getBytes(HTTP_CHARSET)))

  private def ok(data: String, contentType: AsciiString): UResponse =
    Response.http(
      headers = List(Header(CONTENT_TYPE, contentType)),
      content = toContent(data)
    )

  def ok(data: String): UResponse =
    ok(data, TEXT_HTML)

  def okJs(data: String): UResponse =
    ok(data, AsciiString.cached("text/javascript"))

  def seeOther(path: String): UResponse =
    Response.http(
      status = SEE_OTHER,
      headers = List(Header(LOCATION, path))
    )

  def badRequest(message: String): UResponse =
    Response.fromHttpError(HttpError.BadRequest(message))

  def notFound(path: Path): UResponse =
    Response.fromHttpError(HttpError.NotFound(path))

  def serverFailure(failure: Failure): UResponse =
    Response.fromHttpError(HttpError.InternalServerError(failure.message, failure.cause))
}
