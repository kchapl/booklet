package booklet.views

import booklet.model.Book
import scalatags.Text.TypedTag
import scalatags.Text.short._
import scalatags.Text.tags2._

object BookView {

  def list(books: Seq[Book]): TypedTag[String] = html(
    head(
      title("page title")
    ),
    body(
      div(
        h1("This is a title"),
        p("This is a big paragraph of text")
      )
    )
  )
}
