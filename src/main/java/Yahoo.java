import enums.YahooEnum;
import com.github.scribejava.apis.YahooApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import utils.Log;
import utils.Props;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Yahoo {
    private static final Log log = new Log(Yahoo.class);

    private final Scanner scanner;
    private final OAuth20Service service;
    private OAuth2AccessToken currentToken;

    Yahoo() {
        this.scanner = new Scanner(System.in);

        this.service = new ServiceBuilder(YahooEnum.CLIENT_ID.getValue())
                .apiSecret(YahooEnum.CLIENT_SECRET.getValue())
                .callback(OAuthConstants.OOB)
                .build(YahooApi20.instance());
    }

    void authenticate(){
        if(Props.getProperties() != null){
            final String accessToken = Props.getYahooAccessToken();
            final String refreshToken = Props.getYahooRefreshToken();
            final String tokenType = Props.getYahooTokenType();
            final String scope = Props.getYahooTokenScope();
            final String rawResponse = Props.getYahooTokenRawResponse();
            final int expireTime = Props.getYahooTokenExpireTime();

            currentToken = new OAuth2AccessToken(accessToken,tokenType, expireTime, refreshToken, scope, rawResponse);
            final long currentTime = System.currentTimeMillis();
            final long dateRetrieved = Props.getYahooTokenRetrievedTime();

            if(isTokenExpired(currentTime, dateRetrieved, expireTime)){
                refreshExpiredToken();
            }
        } else {
            initialAuthentication();
        }
    }

    private boolean isTokenExpired(long current, long retrieved, int expiresIn){
        final long timeElapsed = ((current - retrieved) / 1000);
        final boolean hasExpired = timeElapsed > expiresIn;
        log.debug("Token expired: " + hasExpired + "\n" +
                "Seconds remaining until expiration: " + (expiresIn - timeElapsed),false);
        return hasExpired;
    }

    private boolean initialAuthentication() {
        try {
            log.trace("=== Yahoo's OAuth Workflow ===", false);

            // Obtain the Request Token
            log.trace("Fetching the Request Token...", false);
            log.trace("Got the Request Token!", false);

            log.trace("Now go and authorize here:", false);
            log.trace(service.getAuthorizationUrl(), false);
            log.trace("And paste the verifier here:", false);
            final String oauthVerifier = scanner.nextLine();

            // Trade the Request Token and Verfier for the Access Token
            log.trace("Trading the Request Token for an Access Token...", false);
            currentToken = service.getAccessToken(oauthVerifier);
            saveAuthenticationData();
            log.trace("Got the Access Token!", false);
            log.trace("Access Token: " + currentToken.getAccessToken(), false);
            log.trace("Refresh Token: " + currentToken.getRefreshToken(), false);

            log.trace("Authorized successfully.", false);

            return true;
        } catch (InterruptedException | ExecutionException |
                IOException e) {
            log.error(e.getLocalizedMessage(), true);

            return false;
        }
    }

    void testUrlDataGrab(){
        try {
            final String PROTECTED_RESOURCE_URL
                    = "https://fantasysports.yahooapis.com/fantasy/v2/users;use_login=1/games/teams";
            // Now let's go and ask for a protected resource!
            log.debug("Now we're going to access a protected resource...", false);
            final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            service.signRequest(currentToken, request);
            final Response response = service.execute(request);
            log.debug("Got it! Lets see what we found...", false);
            log.debug(response.getCode(), false);
            log.debug(response.getBody(), false);
        } catch (IOException | InterruptedException | ExecutionException e){
            log.error(e.getLocalizedMessage(), false);
        }
    }

    void refreshExpiredToken() {
        try {
            currentToken = service.refreshAccessToken(currentToken.getRefreshToken());
            saveAuthenticationData();
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error(e.getLocalizedMessage(), true);
        }
    }

    private void saveAuthenticationData(){
        Props.setYahooTokenData(currentToken);
    }
}
