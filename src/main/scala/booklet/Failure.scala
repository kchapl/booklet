package booklet

case class Failure(message: String, cause: Option[Throwable])

object Failure {
  def apply(t: Throwable): Failure = Failure(t.getMessage, Some(t))

  def apply(msg: String): Failure = Failure(msg, None)
}
