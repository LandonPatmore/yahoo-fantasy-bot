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
    @Deprecated(
        "To be replaced soon with a generic object that the rest of the system can read, thus no reliance on the document object from jsoup",
        ReplaceWith(""),
        DeprecationLevel.WARNING
    )
    fun yahooApiRequest(yahooApiRequest: YahooApiRequest): Document

    /**
     * Gets transactions for league.
     */
    @Deprecated(
        "To be replaced soon with a generic object that the rest of the system can read, thus no reliance on the document object from jsoup",
        ReplaceWith(""),
        DeprecationLevel.WARNING
    )
    fun getTransactions(): Document

    /**
     * Gets standings for league.
     */
    @Deprecated(
        "To be replaced soon with a generic object that the rest of the system can read, thus no reliance on the document object from jsoup",
        ReplaceWith(""),
        DeprecationLevel.WARNING
    )
    fun getStandings(): Document

    /**
     * Gets teams data for league.
     */
    @Deprecated(
        "To be replaced soon with a generic object that the rest of the system can read, thus no reliance on the document object from jsoup",
        ReplaceWith(""),
        DeprecationLevel.WARNING
    )
    fun getTeamsData(): Document
}