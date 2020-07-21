package exceptions

import shared.EnvVariable

class GameKeyException(private val variable: EnvVariable.Str) : Exception() {
    override val message: String
        get() = "Game key (${variable.variable}) is not a valid game key.  Valid options are (NFL, NBA, MLB).  " +
                "Please fix and restart dynos."
}