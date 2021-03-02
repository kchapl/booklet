package services.book_finder

import model.{Author, BookToInsert, Title}
import zio._

object TestBookFinder {
  val impl: ZLayer[Any, Nothing, BookFinder] =
    ZLayer.succeed(_ => Task(Some(BookToInsert(Author("a4"), Title("t7"), None, Some("i8"), None))))
}
