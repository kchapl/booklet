package services.book_finder

import model.{Author, BookToInsert, Isbn, Title}
import zio._

object TestBookFinder {
  val impl: ZLayer[Any, Nothing, BookFinder] =
    ZLayer.succeed(_ =>
      ZIO.some(BookToInsert(Isbn("1234"), Author("a4"), Title("t7"), None, Some("i8"), None))
    )
}
