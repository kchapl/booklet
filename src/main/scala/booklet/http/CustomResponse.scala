package booklet.http

import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.TEXT_HTML
import zhttp.http.HttpData.CompleteData
import zhttp.http.Response.http
import zhttp.http.{HTTP_CHARSET, Header, HttpData, UResponse}
import zio.Chunk

object CustomResponse {

  def htmlString(data: String): UResponse =
    http(
      content = toContent(data),
      headers = List(Header(CONTENT_TYPE, TEXT_HTML))
    )

  def toContent(data: String): CompleteData =
    HttpData.CompleteData(Chunk.fromArray(data.getBytes(HTTP_CHARSET)))
}
