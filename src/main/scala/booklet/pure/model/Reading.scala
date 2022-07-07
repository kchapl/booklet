package booklet.pure.model

import java.time.LocalDate

case class Reading(id: ReadingId, book: Book, completed: LocalDate, rating: Rating)
