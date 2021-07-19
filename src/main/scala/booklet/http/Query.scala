package booklet.http

object Query {

  def fromQueryString(s: String): Map[String, String] = {
    def splitParam(param: String) = {
      val x = param.split("=")
      x(0) -> x(1)
    }
    s.split("&").map(splitParam).toMap
  }
}
