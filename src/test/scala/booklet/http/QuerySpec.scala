package booklet.http

import zio.test._

object QuerySpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment, Any] = test("fromFormBody succeeds") {
    val body =
      """----------------------------138933385763277926868964
        |Content-Disposition: form-data; name="isbn"
        |
        |12345678
        |----------------------------138933385763277926868964
        |Content-Disposition: form-data; name="author"
        |
        |KGB
        |----------------------------138933385763277926868964
        |Content-Disposition: form-data; name="title"
        |
        |Title789
        |----------------------------138933385763277926868964--
        |
        |""".stripMargin
    assertTrue(
      Query
        .fromFormBody(body) == Map("isbn" -> "12345678", "author" -> "KGB", "title" -> "Title789")
    )
  }
}
