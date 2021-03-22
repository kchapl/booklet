package model

sealed trait Failure {
  def reason: String
}

case class BookLookupFailure(reason: String) extends Failure
