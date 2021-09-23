package booklet.http

import booklet.Failure
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.TEXT_HTML
import zhttp.http.HttpData.CompleteData
import zhttp.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND}
import zhttp.http._
import zio.Chunk

object CustomResponse {

  def toContent(data: String): CompleteData =
    HttpData.CompleteData(Chunk.fromArray(data.getBytes(HTTP_CHARSET)))

  def ok(data: String): UHttpResponse =
    Response.http(
      headers = List(Header(CONTENT_TYPE, TEXT_HTML)),
      content = toContent(data)
    )

  def badRequest(message: String): UHttpResponse =
    Response.http(
      status = BAD_REQUEST,
      content = toContent(message)
    )

  def notFound(message: String): UHttpResponse =
    Response.http(
      status = NOT_FOUND,
      content = toContent(message)
    )

  def serverFailure(failure: Failure): UHttpResponse =
    Response.http(
      status = INTERNAL_SERVER_ERROR,
      content = toContent(failure.cause.toString)
    )
}
