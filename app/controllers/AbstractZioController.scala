package controllers

import play.api.mvc._
import zio.{URIO, ZEnv}

abstract class AbstractZioController(components: ControllerComponents)
    extends AbstractController(components) {

  protected def ZioAction: (Request[AnyContent] => URIO[ZEnv, Result]) => Action[AnyContent] =
    AbstractZioController(Action)
}

object AbstractZioController {
  private val runtime = zio.Runtime.default

  def apply(
      action: ActionBuilder[Request, AnyContent]
  )(result: Request[AnyContent] => URIO[ZEnv, Result]): Action[AnyContent] =
    action.async { request =>
      runtime.unsafeRunToFuture(result(request))
    }
}
