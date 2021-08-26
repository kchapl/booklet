package booklet

case class Config(
    app: AppConfig,
    db: DbConfig,
    bookLookup: BookLookupConfig
)

case class AppConfig(
    port: Int
)

case class DbConfig(
    driver: String,
    url: String,
    userName: String,
    password: String
)

case class BookLookupConfig(
    url: String,
    key: String,
    signInClientId: String
)
