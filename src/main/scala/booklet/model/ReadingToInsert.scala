package booklet.model

import java.time.LocalDate

case class ReadingToInsert(bookToInsert: BookToInsert, completed: LocalDate, rating: Rating)
