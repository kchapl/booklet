package booklet.pure.views

import scalatags.Text.TypedTag
import scalatags.Text.all._

object SignInView {

  // See https://developers.google.com/identity/gsi/web/guides/migration#redirect-mode
  val show: TypedTag[String] = html(
    head(
      meta(charset := "UTF-8"),
    ),
    body(
      script(src := "https://accounts.google.com/gsi/client", async, defer),
      div(
        `class` := "g_id_onload",
        attr("data-client_id") :=
          "909653409795-6lsrim2ff07s3rgfjirii96bu2e6qbke.apps.googleusercontent.com",
        attr("data-ux_mode") := "redirect",
        attr("data-login_uri") := "http://localhost:9000/sign-in",
      ),
      div(`class` := "g_id_signin", attr("data-type") := "standard")
    )
  )
}
