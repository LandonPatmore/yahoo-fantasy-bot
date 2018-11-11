package web;

import com.github.scribejava.apis.YahooApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import shared.Log;
import shared.Postgres;
import shared.YahooEnum;

import static spark.Spark.get;
import static spark.Spark.port;

public class Server {
    private static final Log log = new Log(Server.class);
    private static OAuth20Service service;

    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        get("/", (req, res) -> {
            if (Postgres.getLatestTokenData() == null) {
                log.debug("User is not authenticated.  Sending to Yahoo.", false);
                res.redirect(authenticationUrl(req.scheme() + "://" + req.host()));
                return null;
            } else {
                log.debug("User is already authenticated.  Not sending to Yahoo.", false);
                return "You are already authenticated with Yahoo's servers.";
            }
        });

        get("/auth", (req, res) -> {
            // Trade the Request Token and Verfier for the Access Token
            log.trace("Trading request token for access token...", false);
            Postgres.saveTokenData(service.getAccessToken(req.queryParams("code")));
            log.trace("Access token received.  Authorized successfully.", false);
            return "You are authorized";
        });
    }

    /**
     * Gets the authentication url from Yahoo
     * @param url the callback url
     * @return String url
     */
    private static String authenticationUrl(String url) {
        log.trace("Initial authorization...", false);

        service = new ServiceBuilder(YahooEnum.CLIENT_ID.getValue())
                .apiSecret(YahooEnum.CLIENT_SECRET.getValue())
                .callback(url + "/auth")
                .build(YahooApi20.instance());

        return service.getAuthorizationUrl();
    }

    /**
     * Get the assigned port on Heroku
     * @return int port
     */
    private static int getHerokuAssignedPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
