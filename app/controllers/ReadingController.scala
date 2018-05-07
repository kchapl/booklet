package controllers

import model.Book
import play.api.db.Database
import play.api.mvc._

class ReadingController(components: ControllerComponents)(implicit db: Database)
    extends AbstractController(components) {

  def listReadings() = Action { _ =>
    val books = Book.all
    Ok(views.html.readings(books))
  }
}
