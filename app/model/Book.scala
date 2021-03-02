package model

case class Book(
    id: Long,
    author: String,
    title: String,
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)
