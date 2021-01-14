package controllers

import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO}
import config.Config
import doobie._
import doobie.implicits._
import model.Book
import play.api.mvc._
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext

class ReadingController(components: ControllerComponents)
    extends AbstractController(components) {

  def listReadings(): Action[AnyContent] =
    Action { _ =>
      val y = ConfigSource.default.loadOrThrow[Config]
      implicit val cs: ContextShift[IO] =
        IO.contextShift(ExecutionContext.global)
      val xa =
        Transactor.fromDriverManager[IO](
          driver = y.dbDriver,
          url = y.dbUrl,
          user = y.dbUser,
          pass = y.dbPass
        )
      def findAll: ConnectionIO[NonEmptyList[Book]] =
        sql"select id, author, title from books"
          .query[Book]
          .nel
      val x     = findAll.transact(xa).unsafeRunSync()
      val books = x.toList
      Ok(views.html.readings(books))
    }
}
