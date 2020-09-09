package bot.utils

import bot.utils.models.YahooApiRequest
import org.jsoup.nodes.Document

interface IDataRetriever {
    /**
     * Checks to see whether or not a token is expired.
     *
     * @param retrieved when the token was initially retrieved
     * @param expiresIn when the token expires
     * @return if the token is expired
     */
    fun isTokenExpired(retrieved: Long, expiresIn: Int): Boolean

    /**
     * Refreshes an expired token.
     */
    fun refreshExpiredToken()

    /**
     * Gets authentication token from DB if it exists.
     */
    fun getAuthenticationToken()

    /**
     * Grabs data requested from Yahoo.
     */
    fun grabData(url: String): Document

    /**
     * Retrieves the Yahoo Game key for the specified game.
     */
    fun retrieveGameKey(): String?

    /**
     * Makes a request out to Yahoo and returns data.
     */
    fun yahooApiRequest(yahooApiRequest: YahooApiRequest): Document

    /**
     * Gets transactions for league.
     */
    fun getTransactions(): Document

    /**
     * Gets standings for league.
     */
    fun getStandings(): Document

    /**
     * Gets teams data for league.
     */
    fun getTeamsData(): Document
}