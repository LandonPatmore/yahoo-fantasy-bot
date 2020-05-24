package webserver.exceptions

class PostgresException : Exception() {
    override val message: String
        get() = "Could not save token to Postgres Database, please restart dynos."
}