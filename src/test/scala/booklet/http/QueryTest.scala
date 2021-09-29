package booklet.http

class QueryTest extends munit.FunSuite {

  test("fromFormBody succeeds") {
    val body = """----------------------------138933385763277926868964
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
    assertEquals(
      Query.fromFormBody(body),
      Map("isbn" -> "12345678", "author" -> "KGB", "title" -> "Title789")
    )
  }
}
