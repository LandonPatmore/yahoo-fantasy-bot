package webserver.exceptions

class AuthenticationException : Exception() {
    override val message: String
        get() = "Could not authenticate with Yahoo, please restart dynos."
}