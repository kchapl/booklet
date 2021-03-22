package model

case class BookToInsert(
    isbn: ISBN,
    author: Author,
    title: Title,
    subtitle: Option[Subtitle],
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)
