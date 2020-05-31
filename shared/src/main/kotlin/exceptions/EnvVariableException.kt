package exceptions

import shared.EnvVariable

class EnvVariableException(private val variable: EnvVariable) : Exception() {
    override val message: String
        get() = "Environment variable $variable was not entered correctly.  Please fix and restart dynos."
}