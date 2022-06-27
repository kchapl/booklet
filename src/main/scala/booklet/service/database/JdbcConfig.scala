package booklet.service.database

import cats.implicits._

import java.net.URI

case class JdbcConfig(url: String, userName: Option[String], password: Option[String])

object JdbcConfig {

  def fromDbUrl(dbUrl: String): JdbcConfig = {
    val uri = new URI(dbUrl)
    val userInfo = Option(uri.getUserInfo).map(_.split(":"))
    val port = if (uri.getPort === -1) "" else s":${uri.getPort}"
    val sslMode = if (uri.getHost === "localhost") "" else "?sslmode=require"
    JdbcConfig(
      url = s"jdbc:postgresql://${uri.getHost}$port${uri.getPath}$sslMode",
      userName = userInfo.flatMap(_.headOption),
      password = userInfo.flatMap(_.lift(1))
    )
  }
}
