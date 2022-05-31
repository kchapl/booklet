package booklet

object SimpleEquality {
  implicit class Equal[A](val left: A) extends AnyVal {
    def ===(right: A): Boolean = left == right
  }
}
