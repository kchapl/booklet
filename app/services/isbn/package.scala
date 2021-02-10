package services

import model.Book
import zio._

package object isbn {

  type Isbn = Has[Isbn.Service]

  object Isbn {
    trait Service {
      def lookUp(isbn: String): Task[Option[Book]]
    }

    def lookUp(isbn: String): RIO[Isbn, Option[Book]] =
      RIO.accessM(_.get.lookUp(isbn))
  }
}
