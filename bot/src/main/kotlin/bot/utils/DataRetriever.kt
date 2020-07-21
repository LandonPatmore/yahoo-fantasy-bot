package bot.utils

import bot.utils.models.YahooApiRequest
import com.github.scribejava.apis.YahooApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.OAuthConstants
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import shared.EnvVariable
import shared.Postgres

object DataRetriever {
    private const val SCOREBOARD = "/scoreboard"
    private const val STANDINGS = "/standings"
    private const val TRANSACTIONS = "/transactions"
    private const val BASE_URL = "https://fantasysports.yahooapis.com/fantasy/v2"

    private var currentToken: Pair<Long, OAuth2AccessToken>? = null

    private val oauthService = ServiceBuilder(EnvVariable.Str.YahooClientId.variable)
        .apiSecret(EnvVariable.Str.YahooClientSecret.variable)
        .callback(OAuthConstants.OOB)
        .build(YahooApi20.instance())
    private val gameKeyUrl = "/game/${EnvVariable.Str.YahooGameKey.variable}"
    private var leagueUrl: String? = null

    /**
     * Checks to see whether or not a token is expired.
     *
     * @param retrieved when the token was initially retrieved
     * @param expiresIn when the token expires
     * @return if the token is expired
     */
    private fun isTokenExpired(retrieved: Long, expiresIn: Int): Boolean {
        val timeElapsed = ((System.currentTimeMillis() - retrieved) / 1000)
        return timeElapsed >= expiresIn
    }

    /**
     * Refreshes an expired token.
     */
    private fun refreshExpiredToken() {
        currentToken?.let {
            if (isTokenExpired(it.first, it.second.expiresIn)) {
                val refreshToken = oauthService.refreshAccessToken(it.second.refreshToken)
                currentToken = Pair(System.currentTimeMillis(), refreshToken)
                Postgres.saveTokenData(refreshToken)
            }
        }
    }

    /**
     * Gets authentication token from DB if it exists.
     */
    fun getAuthenticationToken() {
        while (true) {
            currentToken = Postgres.latestTokenData
            if (currentToken == null) { // This will run only if there is no data in the database
                println("There is currently no token data in the database.  Please authenticate with Yahoo.")
            } else {
                return
            }

            Thread.sleep(5000)
        }
    }

    /**
     * Grabs data requested from Yahoo.
     */
    private fun grabData(url: String): Document {
        refreshExpiredToken()
        println("Grabbing Data...")
        val request = OAuthRequest(Verb.GET, url)
        oauthService.signRequest(currentToken?.second, request)
        val response = oauthService.execute(request)
        println("Data grabbed.")
        return Jsoup.parse(response.body, "", Parser.xmlParser())
    }

    /**
     * Retrieves the Yahoo Game key for the specified game.
     */
    private fun retrieveGameKey(): String? {
        while (true) {
            val data = grabData(BASE_URL + gameKeyUrl)
            val gameKey = data.select("game_key").first().text()

            if (gameKey.isNullOrEmpty()) {
                println("Game key could not retrieved from Yahoo, retrying in 5 seconds...")
                Thread.sleep(5000)
            }

            return gameKey
        }
    }

    /**
     * Makes a request out to Yahoo and returns data.
     */
    fun yahooApiRequest(yahooApiRequest: YahooApiRequest): Document {
        if (leagueUrl == null) {
            leagueUrl = "/league/${retrieveGameKey()}.l.${EnvVariable.Str.YahooLeagueId.variable}"
        }

        return when (yahooApiRequest) {
            YahooApiRequest.Transactions -> getTransactions()
            YahooApiRequest.Standings -> getStandings()
            YahooApiRequest.TeamsData -> getTeamsData()
        }
    }

    /**
     * Gets transactions for league.
     */
    private fun getTransactions(): Document {
        return grabData(BASE_URL + leagueUrl + TRANSACTIONS)
    }

    /**
     * Gets standings for league.
     */
    private fun getStandings(): Document {
        return grabData(BASE_URL + leagueUrl + STANDINGS)
    }

    /**
     * Gets teams data for league.
     */
    private fun getTeamsData(): Document {
        return grabData(BASE_URL + leagueUrl + SCOREBOARD)
    }
}
