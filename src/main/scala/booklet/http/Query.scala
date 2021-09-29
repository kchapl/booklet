package booklet.http

import zhttp.http.Request

object Query {

  def fromRequest(request: Request): Map[String, String] =
    request.getBodyAsString.map(fromFormBody).getOrElse(Map.empty[String, String])

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
