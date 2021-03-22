package controllers

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

case class Credentials(userName: String, password: String)

object Credentials {
  def fromAuthHeader(authHeader: String): Option[Credentials] = {
    val toDecode = authHeader.replaceFirst("Basic ", "").getBytes(UTF_8)
    val decoded  = new String(Base64.getDecoder.decode(toDecode), UTF_8)
    if (decoded.length >= 3) {
      val Array(user, password) = decoded.split(":")
      Some(Credentials(user, password))
    } else None
  }
}
