package booklet

case class Failure(message: String, cause: Throwable)

object Failure {
  def apply(t: Throwable): Failure = Failure(t.getMessage, t)
}
