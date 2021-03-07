package controllers

import config.Config.config
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import services.book_finder.{BookFinder, LiveBookFinder}
import services.database.{Database, LiveDatabase}
import zio.{ZEnv, ZIO}

abstract class AbstractZioController(components: ControllerComponents)
    extends AbstractController(components) {

  protected def ZioAction: (
      Request[AnyContent] => ZIO[ZEnv with Database with BookFinder, Nothing, Result]
  ) => Action[AnyContent] =
    AbstractZioController(Action)

  protected def ZioAuthorisedAction: (
      Request[AnyContent] => ZIO[ZEnv with Database with BookFinder, Nothing, Result]
  ) => Action[AnyContent] =
    AbstractZioController.authorised(Action)
}

object AbstractZioController {
  private val runtime = zio.Runtime.default

  def apply(
      action: ActionBuilder[Request, AnyContent]
  )(
      result: Request[AnyContent] => ZIO[ZEnv with Database with BookFinder, Nothing, Result]
  ): Action[AnyContent] =
    action.async { request =>
      runtime.unsafeRunToFuture(
        result(request).provideCustomLayer(LiveDatabase.impl ++ LiveBookFinder.impl)
      )
    }

  def authorised(
      action: ActionBuilder[Request, AnyContent]
  )(
      result: Request[AnyContent] => ZIO[ZEnv with Database with BookFinder, Nothing, Result]
  ): Action[AnyContent] =
    action.async { request =>
      val authHeader = request.headers.get("Authorization")
      val authorised = authHeader
        .flatMap(Credentials.fromAuthHeader)
        .exists(credentials =>
          credentials.userName == config.userName && credentials.password == config.password
        )
      runtime.unsafeRunToFuture(
        if (authorised) {
          result(request).provideCustomLayer(LiveDatabase.impl ++ LiveBookFinder.impl)
        } else {
          ZIO.succeed(
            Unauthorized.withHeaders(
              "WWW-Authenticate" -> "Basic realm=\"Booklet realm\", charset=\"UTF-8\""
            )
          )
        }
      )
    }
}
