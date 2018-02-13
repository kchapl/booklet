package controllers

import model.Book
import play.api.mvc._

class App(components: ControllerComponents) extends AbstractController(components) {

  def index() = Action { _ =>
    val books = Seq(
      Book("a1", "t1"),
      Book("a2", "t2"),
      Book("a3", "t3")
    )
    Ok(views.html.readings(books))
  }
}
