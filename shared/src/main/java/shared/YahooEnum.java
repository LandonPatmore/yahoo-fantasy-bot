package shared;

public enum YahooEnum {
    WEEKLY_UPDATE("weekly"),
    SCORE_UPDATE("score"),
    CLOSE_SCORE_UPDATE("close_score");

    private final String value;

    YahooEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
