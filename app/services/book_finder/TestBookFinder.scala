package services.book_finder

import model.Book
import zio._

object TestBookFinder {
  val impl: ZLayer[Any, Nothing, BookFinder] = ZLayer.succeed(_ => Task(Some(Book("a4", "t7"))))
}
