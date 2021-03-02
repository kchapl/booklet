package model

case class BookToInsert(
    author: String,
    title: String,
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)
