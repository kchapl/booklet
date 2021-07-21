package booklet.views

import booklet.model.Book
import scalatags.Text.all._

object BookView {

  def list(books: Seq[Book]) = html(
    head(),
    body(
      for (book <- books)
        yield div(
          p(book.id.value),
          p(book.isbn.value),
          p(book.title.value),
          p(book.author.value)
        )
    )
  )
}
