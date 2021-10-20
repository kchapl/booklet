package booklet

case class Failure(message: String, cause: Option[Throwable])

object Failure {
  def fromThrowable(t: Throwable): Failure = Failure(t.getMessage, Some(t))
}
