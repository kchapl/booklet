package controllers

import play.api.mvc._
import services.book_finder.BookFinder
import zio.ZIO
import upickle.default._

class BookController(components: ControllerComponents) extends AbstractZioController(components) {

  def showAddForm(): Action[AnyContent] =
    ZioAuthorisedAction(_ => ZIO.succeed(Ok(views.html.bookAdd())))

  def add(): Action[AnyContent] = TODO

  def lookUp(isbn: String): Action[AnyContent] =
    ZioAuthorisedAction { _ =>
      BookFinder
        .findByIsbn(isbn)
        .fold(
          e => InternalServerError(e.reason),
          {
            case Some(book) => Ok(write(book)).as("application/json")
            case None       => NotFound
          }
        )
    }
}
