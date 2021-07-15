package booklet.model

import java.time.LocalDate

case class ReadingData(bookData: BookData, completed: LocalDate, rating: Rating)
