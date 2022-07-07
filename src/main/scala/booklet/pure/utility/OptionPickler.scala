package booklet.pure.utility

// See https://com-lihaoyi.github.io/upickle/#CustomConfiguration
object OptionPickler extends upickle.AttributeTagged {
  implicit override def OptionReader[T: Reader]: Reader[Option[T]] =
    new Reader.Delegate[Any, Option[T]](implicitly[Reader[T]].map(Some(_))) {
      override def visitNull(index: Int): Option[T] = None
    }
}
