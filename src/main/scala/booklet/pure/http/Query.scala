package booklet.pure.http

import booklet.pure.Failure
import zhttp.http.Request
import zio.IO

object Query {

  def param(request: Request)(name: String): Option[String] =
    request.url.queryParams.get(name).flatMap(_.headOption)

  def fromRequest(request: Request): IO[Failure, Map[String, String]] =
    request.bodyAsString.mapBoth(Failure.fromThrowable, fromFormBody)

  def fromFormBody(body: String): Map[String, String] =
    body
      .split("\n")
      .filterNot(_.startsWith("----------------------------"))
      .grouped(3)
      .map { field =>
        val name = field(0).trim
          .stripPrefix("Content-Disposition: form-data; name=\"")
          .stripSuffix("\"")
        val value = field(2).trim
        name -> value
      }
      .toMap
}
