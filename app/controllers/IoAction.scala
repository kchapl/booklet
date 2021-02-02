package controllers

import cats.effect.IO
import play.api.mvc._

object IoAction {

  def apply(action: ActionBuilder[Request, AnyContent])(
      result: Request[AnyContent] => IO[Result]
  ): Action[AnyContent] =
    action.async(request => result(request).unsafeToFuture())
}
