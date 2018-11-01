import entities.Yahoo;
import enums.YahooEnum;
import utils.Props;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Props.loadSettings();
                Yahoo.authenticate();
                Yahoo.grabData("https://fantasysports.yahooapis.com/fantasy/v2/league/" + YahooEnum.LEAGUE_KEY.getValue() + "/standings");
            }
        };

        final Timer timer = new Timer();
        timer.schedule(timerTask, 0, 60 * 1000);
    }
}
