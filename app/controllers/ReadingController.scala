package controllers

import model.{Book, Reading}
import play.api.mvc._

import java.time.LocalDate

class ReadingController(components: ControllerComponents)
    extends AbstractZioController(components) {

  def listBooks(): Action[AnyContent] =
    ZioAction(_ =>
      Book
        .fetchAll()
        .fold(
          e => InternalServerError(e.getMessage),
          books => Ok(views.html.books(books))
        )
    )

  def listReadings(): Action[AnyContent] =
    ZioAction { _ =>
      Reading
        .fetchAll()
        .fold(
          e => InternalServerError(e.getMessage),
          readings => Ok(views.html.readings(readings))
        )
    }

  def createReading(): Action[AnyContent] =
    ZioAction { _ =>
      val reading = Reading(Book("a3", "t3"), LocalDate.now, 3)
      Reading
        .insert(reading)
        .fold(
          e => InternalServerError(e.getMessage),
          _ => Redirect(routes.ReadingController.listReadings())
        )
    }
}
