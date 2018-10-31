package enums;

import utils.Props;

public enum YahooEnum implements EnumInterface {
    // URLS
    BASE_URL("https://fantasysports.yahooapis.com/fantasy/v2/"),
    LEAGUE_ENDPOINT("league/"),
    LEAGUE_KEY("nfl.l." + Props.getYahooLeagueId()),


    // Authentication
    CLIENT_ID(Props.getYahooClientId()),
    CLIENT_SECRET(Props.getYahooClientSecret());

    private final String value;

    YahooEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
