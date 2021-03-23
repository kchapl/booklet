package controllers

import model._
import play.api.mvc._
import services.database.Database
import zio.ZIO

import java.time.LocalDate

class ReadingController(components: ControllerComponents)
    extends AbstractZioController(components) {

  def list(): Action[AnyContent] =
    ZioAuthorisedAction { _ =>
      Database
        .fetchAllReadings()
        .fold(
          e => InternalServerError(e.getMessage),
          readings => Ok(views.html.readings(readings))
        )
    }

  def showAddForm(): Action[AnyContent] =
    ZioAuthorisedAction(_ => ZIO.succeed(Ok(views.html.readingAdd())))

  def add(): Action[AnyContent] =
    ZioAuthorisedAction { request =>
      (for {
        params <- ZIO
          .fromOption(request.body.asFormUrlEncoded)
          .orElseFail(BadRequest("Missing form"))
        isbn <- ZIO
          .fromOption(params.get("isbn").flatMap(_.headOption))
          .orElseFail(BadRequest("Missing ISBN"))
        author <- ZIO
          .fromOption(params.get("author").flatMap(_.headOption))
          .orElseFail(BadRequest("Missing author"))
        title <- ZIO
          .fromOption(params.get("title").flatMap(_.headOption))
          .orElseFail(BadRequest("Missing title"))
        completed <- ZIO
          .fromOption(params.get("completed").flatMap(_.headOption.map(y => LocalDate.parse(y))))
          .orElseFail(BadRequest("Missing completed"))
        rating <- ZIO
          .fromOption(params.get("rating").flatMap(_.headOption.map(_.toInt)))
          .orElseFail(BadRequest("Missing rating"))
        _ <- Database
          .insertReading(
            ReadingToInsert(
              BookToInsert(Isbn(isbn), Author(author), Title(title), None, None, None),
              completed,
              Rating(rating)
            )
          )
          .mapError(e => InternalServerError(e.getMessage))
      } yield ()).fold(identity, _ => Redirect(routes.ReadingController.list()))
    }

  def remove(): Action[AnyContent] =
    ZioAuthorisedAction { _ =>
      val reading = Reading(
        1,
        Book(2, Isbn("1234"), Author("a3"), Title("t3"), None, Some("i4"), None),
        LocalDate.now,
        Rating(3)
      )
      Database
        .deleteReading(reading)
        .fold(
          e => InternalServerError(e.getMessage),
          _ => Redirect(routes.ReadingController.list())
        )
    }
}
