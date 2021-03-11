package controllers

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import config.Config.config
import play.api.mvc._
import zio.ZIO

import java.util.Collections.singletonList

class AuthController(components: ControllerComponents) extends AbstractZioController(components) {

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
}
