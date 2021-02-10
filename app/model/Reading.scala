package model

import java.time.LocalDate

case class Reading(book: Book, completed: LocalDate, rating: Int)
