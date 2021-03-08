package controllers

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import config.Config.config
import model._
import play.api.mvc._
import services.book_finder.BookFinder
import services.database.Database
import zio.ZIO

import java.time.LocalDate
import java.util.Collections.singletonList

class ReadingController(components: ControllerComponents)
    extends AbstractZioController(components) {

  def logIn(): Action[AnyContent] =
    ZioAction { request =>
      ZIO.succeed {
        import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
        val transport   = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance
        val verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
          .setAudience(singletonList(config.signInClientId))
          .build
        val payload2      = request.body.asFormUrlEncoded.get
        val idTokenString = payload2.getOrElse("idtoken", Nil).head
        val idToken       = verifier.verify(idTokenString)
        if (idToken != null) {
          val payload = idToken.getPayload
          val userId  = payload.getSubject
          System.out.println("User ID: " + userId)
          val email         = payload.getEmail
          val emailVerified = payload.getEmailVerified
          val name          = payload.get("name").asInstanceOf[String]
          val locale        = payload.get("locale").asInstanceOf[String]
          val familyName    = payload.get("family_name").asInstanceOf[String]
          val givenName     = payload.get("given_name").asInstanceOf[String]
          // Use or store profile information
          println(email)
          println(emailVerified)
          println(name)
          println(locale)
          println(familyName)
          println(givenName)
          Ok(email)
        } else {
          InternalServerError("Invalid ID token.")
        }
      }
    }

  def listReadings(): Action[AnyContent] =
    ZioAuthorisedAction { _ =>
      Database
        .fetchAllReadings()
        .fold(
          e => InternalServerError(e.getMessage),
          readings => Ok(views.html.readings(readings))
        )
    }

  def createReading(): Action[AnyContent] =
    ZioAuthorisedAction { _ =>
      Database
        .insertReading(
          ReadingToInsert(
            BookToInsert(Author("a3"), Title("t3"), None, None, None),
            LocalDate.now,
            Rating(3)
          )
        )
        .fold(
          e => InternalServerError(e.getMessage),
          _ => Redirect(routes.ReadingController.listReadings())
        )
    }

  def deleteReading(): Action[AnyContent] =
    ZioAuthorisedAction { _ =>
      val reading = Reading(
        1,
        Book(2, Author("a3"), Title("t3"), None, Some("i4"), None),
        LocalDate.now,
        Rating(3)
      )
      Database
        .deleteReading(reading)
        .fold(
          e => InternalServerError(e.getMessage),
          _ => Redirect(routes.ReadingController.listReadings())
        )
    }

  def lookUpBook(isbn: String): Action[AnyContent] =
    ZioAuthorisedAction { _ =>
      BookFinder
        .findByIsbn(isbn)
        .fold(
          e => InternalServerError(e.getMessage), {
            case Some(book) => Ok(views.html.books(Seq(book)))
            case None       => NotFound
          }
        )
    }
}
