package controllers

import play.api.mvc._
import zio.{ZEnv, ZIO}

abstract class AbstractZioController(components: ControllerComponents)
    extends AbstractController(components) {

  protected def ZioAction
      : (Request[AnyContent] => ZIO[ZEnv, Throwable, Result]) => Action[AnyContent] =
    AbstractZioController(Action)
}

object AbstractZioController {
  private val runtime = zio.Runtime.default

  def apply(
      action: ActionBuilder[Request, AnyContent]
  )(result: Request[AnyContent] => ZIO[ZEnv, Throwable, Result]): Action[AnyContent] =
    action.async { request =>
      runtime.unsafeRunToFuture(result(request))
    }
}
