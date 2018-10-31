import entities.Yahoo;
import enums.YahooEnum;
import utils.Props;

public class Main {
    public static void main(String[] args) {
        Props.loadSettings();
        Yahoo.authenticate();
        Yahoo.testUrlDataGrab("https://fantasysports.yahooapis.com/fantasy/v2/league/" + YahooEnum.LEAGUE_KEY.getValue() + "/transactions;team_key={team_key};type=trade");
    }
}
