package booklet.pure

case class Failure(message: String, cause: Option[Throwable])

object Failure {
  def fromThrowable(t: Throwable): Failure = Failure(t.getMessage, Some(t))

  def fromDecodingException(s: String): Failure = Failure(s"Json decoding exception: $s", None)
}
