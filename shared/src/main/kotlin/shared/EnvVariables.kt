package shared

sealed class EnvVariables(val variable: String?) {
    object YahooClientId : EnvVariables(System.getenv("YAHOO_CLIENT_ID"))
    object YahooClientSecret : EnvVariables(System.getenv("YAHOO_CLIENT_SECRET"))
    object YahooLeagueId : EnvVariables(System.getenv("YAHOO_LEAGUE_ID"))
    object GroupMeBotId : EnvVariables(System.getenv("GROUP_ME_BOT_ID"))
    object DiscordWebhookUrl : EnvVariables(System.getenv("DISCORD_WEBHOOK_URL"))
    object SlackWebhookUrl : EnvVariables(System.getenv("SLACK_WEBHOOK_URL"))
    object Port : EnvVariables(System.getenv("PORT"))
    object JdbcDatabaseUrl : EnvVariables(System.getenv("JDBC_DATABASE_URL"))
}
