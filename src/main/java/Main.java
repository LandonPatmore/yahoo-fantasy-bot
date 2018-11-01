import entities.Yahoo;
import enums.YahooEnum;
import helpers.YahooTeam;
import org.jsoup.nodes.Document;
import utils.Props;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");

//        final TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
        Props.loadSettings();
        Yahoo.authenticate();
        final Document doc = Yahoo.grabData("https://fantasysports.yahooapis.com/fantasy/v2/league/" + YahooEnum.LEAGUE_KEY.getValue() + "/standings");
        final HashMap<String, YahooTeam> standings = Yahoo.getStandings(doc);
        final Document doc1 = Yahoo.grabData("https://fantasysports.yahooapis.com/fantasy/v2/league/" + YahooEnum.LEAGUE_KEY.getValue() + "/scoreboard");
        Yahoo.getMatchups(doc1, standings);
//            }
//        };
//
//        final Timer timer = new Timer();
//        timer.schedule(timerTask, 0, 60 * 1000);
    }
}
