package model

import cats.effect.{ContextShift, IO}
import doobie.ExecutionContexts
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor.Aux
import org.scalatest.funsuite.AnyFunSuite

class BookQueriesTestSuite extends AnyFunSuite with IOChecker {

  override val transactor: Aux[IO, Unit] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
    Book.xa
  }

  test("fetchAll")(check(Book.Queries.fetchAll))
}
