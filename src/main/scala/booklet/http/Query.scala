package booklet.http

import zhttp.http.HttpData.CompleteData
import zhttp.http.{HTTP_CHARSET, Request}

object Query {

  def fromRequest(request: Request): Map[String, String] =
    request.content match {
      case CompleteData(data) => Query.fromQueryString(new String(data.toArray, HTTP_CHARSET))
      case _                  => Map.empty[String, String]
    }

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
