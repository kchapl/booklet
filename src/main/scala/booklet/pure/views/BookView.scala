package booklet.pure.views

import booklet.pure.model.Book
import scalatags.Text.all._
import scalatags.Text.tags2.title

object BookView {

  def list(books: Seq[Book]) =
    "<!DOCTYPE html>" +
      html(lang := "en")(
        head(
          meta(charset := "utf-8"),
          meta(name := "viewport", content := "width=device-width, initial-scale=1"),
          link(
            href := "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css",
            rel := "stylesheet",
            integrity := "sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3",
            crossorigin := "anonymous"
          ),
          title("Books")
        ),
        body(
          for (book <- books)
            yield div(cls := "card", style := "width: 18rem;")(
              div(cls := "card-body")(
                p(book.id.value),
                p(book.isbn.value),
                p(book.title.value),
                p(book.author.value)
              )
            )
        )
      )
}
