package controllers

import model.{Book, Reading}
import play.api.mvc._

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
}
