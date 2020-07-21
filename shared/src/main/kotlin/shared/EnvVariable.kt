package shared

sealed class EnvVariable {
    sealed class Str(val variable: String, val optional: Boolean = false) : EnvVariable() {
        object YahooClientId : Str(System.getenv("YAHOO_CLIENT_ID") ?: "")
        object YahooClientSecret : Str(System.getenv("YAHOO_CLIENT_SECRET") ?: "")

        // Don't know if yahoo cares about the case, but looking at docs, they are lower case
        // It shouldn't matter, but knowing Yahoo, it does
        object YahooGameKey : Str(System.getenv("YAHOO_GAME_KEY")?.toLowerCase() ?: "")
        object YahooLeagueId : Str(System.getenv("YAHOO_LEAGUE_ID") ?: "")
        object GroupMeBotId : Str(System.getenv("GROUP_ME_BOT_ID") ?: "", true)
        object DiscordWebhookUrl : Str(System.getenv("DISCORD_WEBHOOK_URL") ?: "", true)
        object SlackWebhookUrl : Str(System.getenv("SLACK_WEBHOOK_URL") ?: "", true)
        object JdbcDatabaseUrl : Str(System.getenv("JDBC_DATABASE_URL") ?: "")
    }

    sealed class Bool(val variable: Boolean) : EnvVariable() {
        object OptInCloseScore : Bool(System.getenv("OPT_IN_CLOSE_SCORE")?.toBoolean() ?: false)
    }

    sealed class Integer(val variable: Int) : EnvVariable() {
        object Port : Integer(System.getenv("PORT")?.toIntOrNull() ?: -1)
    }
}
