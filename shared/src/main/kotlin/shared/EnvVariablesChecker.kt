package shared

import exceptions.EnvVariableException
import exceptions.GameKeyException

object EnvVariablesChecker {
    fun check() {
        checkString(EnvVariable.Str.YahooClientId)
        checkString(EnvVariable.Str.YahooClientSecret)
        checkGameKey(EnvVariable.Str.YahooGameKey)
        checkString(EnvVariable.Str.YahooLeagueId)
        checkString(EnvVariable.Str.GroupMeBotId)
        checkString(EnvVariable.Str.DiscordWebhookUrl)
        checkString(EnvVariable.Str.SlackWebhookUrl)
        checkString(EnvVariable.Str.JdbcDatabaseUrl)
        checkInt(EnvVariable.Integer.Port)
        // No need to check booleans as they are false if they don't exist or malformed
    }

    private fun checkGameKey(env: EnvVariable.Str) {
        checkString(env)

        when (env.variable.toLowerCase()) {
            "nfl", "nba", "mlb" -> return
            else -> throw GameKeyException(env)
        }
    }

    private fun checkString(env: EnvVariable.Str) {
        if (!env.optional && env.variable.trim().isEmpty()) {
            throw EnvVariableException(env)
        }
    }

    private fun checkInt(env: EnvVariable.Integer) {
        if (env.variable == -1) {
            throw EnvVariableException(env)
        }
    }
}