package bot.utils.models

sealed class YahooApiRequest {
    object Transactions : YahooApiRequest()
    object Standings : YahooApiRequest()
    object TeamsData : YahooApiRequest()
}