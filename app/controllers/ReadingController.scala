package controllers

import model.Book
import play.api.mvc._

class ReadingController(components: ControllerComponents) extends AbstractController(components) {

  def listReadings(): Action[AnyContent] = IoAction(Action) { _ =>
    Book.findAll().map(books => Ok(views.html.readings(books.toList)))
  }
}
