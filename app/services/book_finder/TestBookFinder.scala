package services.book_finder

import model.Book
import zio._

object TestBookFinder {
  val impl: ZLayer[Any, Nothing, BookFinder] =
    ZLayer.succeed(_ => Task(Some(Book(1, "a4", "t7", Some("i8"), None))))
}
