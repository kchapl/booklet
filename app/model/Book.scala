package model

case class Book(
    author: String,
    title: String,
    thumbnail: Option[String],
    smallThumbnail: Option[String]
)
