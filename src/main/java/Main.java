import entities.Yahoo;
import enums.YahooEnum;
import utils.Props;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Props.loadSettings();
                Yahoo.authenticate();
                Yahoo.grabData("https://fantasysports.yahooapis.com/fantasy/v2/league/" + YahooEnum.LEAGUE_KEY.getValue() + "/transactions");
            }
        };

        final Timer timer = new Timer();
        timer.schedule(timerTask, 0, 60 * 1000);
    }
}
