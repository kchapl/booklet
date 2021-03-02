package model

case class BookToInsert(
    author: Author,
    title: Title,
    subtitle: Option[Subtitle],
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)
