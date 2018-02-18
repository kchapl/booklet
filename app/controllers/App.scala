package controllers

import model.Book
import play.api.mvc._
import slick.basic.DatabaseConfig
import slick.jdbc.H2Profile

import scala.concurrent.ExecutionContext.Implicits.global

class App(components: ControllerComponents)(implicit dbConfig: DatabaseConfig[H2Profile])
    extends AbstractController(components) {

  def index() = Action.async { _ =>
    Book.setup()

//    val books = Seq(
//      Book(1, "a1", "t1"),
//      Book(2, "a2", "t2"),
//      Book(3, "a3", "t3")
//    )
    Book.fetchAll() map { books =>
      Ok(views.html.readings(books))
    }
  }
}
