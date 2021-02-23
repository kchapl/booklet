package services

import zio._

package object book_finder {
  type BookFinder = Has[BookFinder.Service]
}
