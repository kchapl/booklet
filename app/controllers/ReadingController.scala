package controllers

import model.{Book, Reading}
import play.api.mvc._

class ReadingController(components: ControllerComponents)
    extends AbstractZioController(components) {

  def listBooks(): Action[AnyContent] =
    ZioAction(_ => Book.fetchAll().map(books => Ok(views.html.books(books))))

  def listReadings(): Action[AnyContent] =
    ZioAction(_ => Reading.fetchAll().map(readings => Ok(views.html.readings(readings))))
}
