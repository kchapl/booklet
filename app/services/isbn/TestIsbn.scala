package services.isbn

import model.Book
import zio.{Task, ULayer, ZLayer}

object TestIsbn {
  val impl: ULayer[Isbn] = ZLayer.succeed(_ => Task(Some(Book("a4", "t7"))))
}
