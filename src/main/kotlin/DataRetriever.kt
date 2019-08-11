import com.github.scribejava.apis.YahooApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.OAuthConstants
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.model.OAuthRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

object DataRetriever {
    const val BASE_URL = "https://fantasysports.yahooapis.com/fantasy/v2"
    const val LEAGUE_KEY = "nfl.l." + "test_league_id"
    private val oauthService = ServiceBuilder("test_apiKey")
        .apiSecret("test_apiSecret")
        .callback(OAuthConstants.OOB)
        .build(YahooApi20.instance())
    private var currentToken: Pair<Long, OAuth2AccessToken>? = null

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
        currentToken?.second?.let {
            val refreshToken = oauthService.refreshAccessToken(it.refreshToken)
            currentToken = Pair(System.currentTimeMillis(), refreshToken)
        }
    }

    fun authenticate() {
        currentToken?.let {
            if (isTokenExpired(it.first, it.second.expiresIn)) {
                refreshExpiredToken()
            }
        } ?: run {
            println("Authorize usage here: ${oauthService.authorizationUrl}")
            println("Paste the verification string:")
            val oauthVerifier = readLine()

            // Trade the Request Token and Verifier for the Access Token
            println("Trading request token for access token...")
            currentToken = Pair(System.currentTimeMillis(), oauthService.getAccessToken(oauthVerifier))
            println("Access token received.  Authorized successfully.")
        }
    }

    fun grabData(url: String): Document {
        authenticate()
        println("Grabbing Data...")
        val request = OAuthRequest(Verb.GET, url)
        oauthService.signRequest(currentToken?.second, request)
        val response = oauthService.execute(request)
        println("Data grabbed.")
        return Jsoup.parse(response.body, "", Parser.xmlParser())
    }
}