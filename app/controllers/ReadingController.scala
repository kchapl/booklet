package controllers

import model._
import play.api.mvc._
import services.book_finder.BookFinder
import services.database.Database

import java.time.LocalDate

class ReadingController(components: ControllerComponents)
    extends AbstractZioController(components) {

  def listReadings(): Action[AnyContent] =
    ZioAction { _ =>
      Database
        .fetchAllReadings()
        .fold(
          e => InternalServerError(e.getMessage),
          readings => Ok(views.html.readings(readings))
        )
    }

  def createReading(): Action[AnyContent] =
    ZioAction { _ =>
      Database
        .insertReading(
          ReadingToInsert(
            BookToInsert(Author("a3"), Title("t3"), None, None, None),
            LocalDate.now,
            Rating(3)
          )
        )
        .fold(
          e => InternalServerError(e.getMessage),
          _ => Redirect(routes.ReadingController.listReadings())
        )
    }

  def deleteReading(): Action[AnyContent] =
    ZioAction { _ =>
      val reading = Reading(
        1,
        Book(2, Author("a3"), Title("t3"), None, Some("i4"), None),
        LocalDate.now,
        Rating(3)
      )
      Database
        .deleteReading(reading)
        .fold(
          e => InternalServerError(e.getMessage),
          _ => Redirect(routes.ReadingController.listReadings())
        )
    }

  def lookUpBook(isbn: String): Action[AnyContent] =
    ZioAction { _ =>
      BookFinder
        .findByIsbn(isbn)
        .fold(
          e => InternalServerError(e.getMessage),
          {
            case Some(book) => Ok(views.html.books(Seq(book)))
            case None       => NotFound
          }
        )
    }
}
