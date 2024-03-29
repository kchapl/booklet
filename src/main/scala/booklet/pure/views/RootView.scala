package booklet.pure.views

import scalatags.Text
import scalatags.Text.all._

object RootView {

  // See https://developers.google.com/identity/sign-in/web
  val show: Text.TypedTag[String] = html(
    head(
      meta(charset := "UTF-8"),
      meta(name := "google-signin-scope", content := "profile email"),
      meta(
        name := "google-signin-client_id",
        content := "909653409795-6lsrim2ff07s3rgfjirii96bu2e6qbke.apps.googleusercontent.com"
      ),
      script(src := "https://apis.google.com/js/platform.js", defer)
    ),
    body(
      div(
        `class` := "g-signin2",
        attr("data-onsuccess") := "onSignIn",
        attr("data-theme") := "dark"
      ),
      script(src := "javascript/onSignIn.js")
    )
  )
}
