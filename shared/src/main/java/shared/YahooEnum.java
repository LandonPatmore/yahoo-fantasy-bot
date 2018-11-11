package shared;

public enum YahooEnum implements EnumInterface {
    // Authentication
    CLIENT_ID(System.getenv("YAHOO_CLIENT_ID")),
    CLIENT_SECRET(System.getenv("YAHOO_CLIENT_SECRET")),
    WEEKLY_UPDATE("weekly"),
    SCORE_UPDATE("score"),
    CLOSE_SCORE_UPDATE("close_score");

    private final String value;

    YahooEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
