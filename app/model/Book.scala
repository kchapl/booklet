package model

case class Book(
    id: Long,
    isbn: Isbn,
    author: Author,
    title: Title,
    subtitle: Option[Subtitle],
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)
