package controllers

import model.Book
import play.api.mvc._

class App(components: ControllerComponents)
    extends AbstractController(components) {

  def index() = Action { _ =>
//    Book.setup()

//    val books = Seq(
//      Book(1, "a1", "t1"),
//      Book(2, "a2", "t2"),
//      Book(3, "a3", "t3")
//    )
//    Book.fetchAll() map { books =>
//      Ok(views.html.readings(books))
//    }
    Ok
  }
}
