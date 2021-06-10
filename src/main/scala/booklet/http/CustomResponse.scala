package booklet.http

import io.netty.handler.codec.http.{HttpHeaderNames, HttpHeaderValues}
import zhttp.http.Response.http
import zhttp.http.{HTTP_CHARSET, Header, HttpData, UResponse}
import zio.Chunk

object CustomResponse {
  def htmlString(data: String): UResponse =
    http(
      content = HttpData.CompleteData(Chunk.fromArray(data.getBytes(HTTP_CHARSET))),
      headers = List(Header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_HTML))
    )
}
