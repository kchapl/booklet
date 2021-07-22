package booklet.http

import zhttp.http.Request

object Query {

  def fromRequest(request: Request): Map[String, String] =
    request.getBodyAsString.map(fromQueryString).getOrElse(Map.empty[String, String])

  def fromQueryString(s: String): Map[String, String] = {
    def splitParam(param: String): Option[(String, String)] = {
      val parts = param.split("=")
      for {
        name <- parts.lift(0)
        value <- parts.lift(1)
      } yield name -> value
    }
    s.split("&").flatMap(splitParam).toMap
  }
}
