package model

case class Book(
    id: Long,
    ISBN: ISBN,
    author: Author,
    title: Title,
    subtitle: Option[Subtitle],
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)
