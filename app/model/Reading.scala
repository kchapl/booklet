package model

import java.time.LocalDate

case class Reading(id: Long, book: Book, completed: LocalDate, rating: Rating)
