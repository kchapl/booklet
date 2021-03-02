package controllers

import model.{Book, BookToInsert, Reading, ReadingToInsert}
import play.api.mvc._
import services.book_finder.{BookFinder, LiveBookFinder}
import services.database.{Database, LiveDatabase}

import java.time.LocalDate

class ReadingController(components: ControllerComponents)
    extends AbstractZioController(components) {

  def listReadings(): Action[AnyContent] =
    ZioAction { _ =>
      Database
        .fetchAllReadings()
        .provideCustomLayer(LiveDatabase.impl)
        .fold(
          e => InternalServerError(e.getMessage),
          readings => Ok(views.html.readings(readings))
        )
    }

  def createReading(): Action[AnyContent] =
    ZioAction { _ =>
      Database
        .insertReading(ReadingToInsert(BookToInsert("a3", "t3", None, None), LocalDate.now, 3))
        .provideCustomLayer(LiveDatabase.impl)
        .fold(
          e => InternalServerError(e.getMessage),
          _ => Redirect(routes.ReadingController.listReadings())
        )
    }

  def deleteReading(): Action[AnyContent] =
    ZioAction { _ =>
      val reading = Reading(1, Book(2, "a3", "t3", Some("i4"), None), LocalDate.now, 3)
      Database
        .deleteReading(reading)
        .provideCustomLayer(LiveDatabase.impl)
        .fold(
          e => InternalServerError(e.getMessage),
          _ => Redirect(routes.ReadingController.listReadings())
        )
    }

  def lookUpBook(isbn: String): Action[AnyContent] =
    ZioAction { _ =>
      BookFinder
        .findByIsbn(isbn)
        .provideCustomLayer(LiveBookFinder.impl)
        .fold(
          e => InternalServerError(e.getMessage),
          {
            case Some(book) => Ok(views.html.books(Seq(book)))
            case None       => NotFound
          }
        )
    }
}
