package booklet.pure.views

import booklet.pure.model.Reading
import scalatags.Text.all._

object ReadingView {

  def list(readings: Seq[Reading]) = html(
    head(),
    body(
      for (reading <- readings)
        yield div(
          p(reading.id.value),
          p(reading.book.id.value),
          p(reading.book.isbn.value),
          p(reading.book.title.value),
          p(reading.book.author.value),
          p(reading.completed.toString),
          p(reading.rating.value)
        )
    )
  )
}
