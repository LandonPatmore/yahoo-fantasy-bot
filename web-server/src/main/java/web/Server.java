package web;

import com.github.scribejava.apis.YahooApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import utils.EnvHandler;
import utils.Postgres;
import worker.Bot;

import static spark.Spark.*;

public class Server {
    private static final Logger log = LogManager.getLogger(Server.class);
    private static OAuth20Service service;

    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        registerRoutes();

        Bot.start();
    }

    private static void registerRoutes() {
        get("/", (req, res) -> {
            if (Postgres.getLatestTokenData() == null) {
                log.debug("User is not authenticated.  Sending to Yahoo.");
                res.redirect(authenticationUrl(req.scheme() + "://" + req.host()));
                return null;
            } else {
                log.debug("User is already authenticated.  Not sending to Yahoo.");
                return "You are already authenticated with Yahoo's servers.";
            }
        });

        get("/auth", (req, res) -> {
            Postgres.saveTokenData(service.getAccessToken(req.queryParams("code")));
            log.trace("Access token received.  Authorized successfully.");
            return "You are authorized";
        });

        post("/message", (req, res) -> {
            if(req.body() != null && !req.body().isEmpty()) {
                log.debug("POST Request received to /message endpoint");
                log.debug(req.body());
                final JSONObject j = new JSONObject(req.body());
                CommandInterpreter.interpretCommand(j.getString("text"));
            }
            return null;
        });

        get("/keepalive", (req, res) -> true);
    }

    /**
     * Gets the authentication url from Yahoo
     *
     * @param url the callback url
     * @return String url
     */
    private static String authenticationUrl(String url) {
        log.trace("Initial authorization...");

        service = new ServiceBuilder(EnvHandler.YAHOO_CLIENT_ID.getValue())
                .apiSecret(EnvHandler.YAHOO_CLIENT_SECRET.getValue())
                .callback(url + "/auth")
                .build(YahooApi20.instance());

        return service.getAuthorizationUrl();
    }

    /**
     * Get the assigned port on Heroku
     *
     * @return int port
     */
    private static int getHerokuAssignedPort() {
        final String port = EnvHandler.PORT.getValue();
        if (port != null) {
            return Integer.parseInt(port);
        }
        return 4567;
    }
}
