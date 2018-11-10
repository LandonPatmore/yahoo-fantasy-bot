package web;

import com.github.scribejava.apis.YahooApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.oauth.OAuth20Service;
import enums.YahooEnum;
import services.Yahoo;
import utils.Log;
import utils.Postgres;

import static spark.Spark.get;
import static spark.Spark.port;

public class Server {
    private static final Log log = new Log(Server.class);

    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        get("/", (req, res) -> {
            if (Postgres.getLatestTokenData() == null) {
                res.redirect(Yahoo.authenticationUrl(req.scheme() + "://" + req.host()));
                return null;
            } else {
                return "You are already authenticated with Yahoo's servers.";
            }
        });

        get("/auth", (req, res) -> {
            final OAuth20Service service = new ServiceBuilder(YahooEnum.CLIENT_ID.getValue())
                    .apiSecret(YahooEnum.CLIENT_SECRET.getValue())
                    .callback(OAuthConstants.OOB)
                    .build(YahooApi20.instance());
            // Trade the Request Token and Verfier for the Access Token
            log.trace("Trading request token for access token...", false);
            Yahoo.setToken(Yahoo.getService().getAccessToken(req.queryParams("code")));
            Yahoo.saveAuthenticationData();
            log.trace("Access token received.  Authorized successfully.", false);
            return "You are authorized";
        });
    }

    private static int getHerokuAssignedPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
