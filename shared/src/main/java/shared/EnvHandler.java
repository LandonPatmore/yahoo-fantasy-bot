package shared;

public enum EnvHandler implements EnumInterface {

    YAHOO_CLIENT_ID(System.getenv("YAHOO_CLIENT_ID")),
    YAHOO_CLIENT_SECRET(System.getenv("YAHOO_CLIENT_SECRET")),
    YAHOO_LEAGUE_ID(System.getenv("YAHOO_LEAGUE_ID")),
    GROUP_ME_BOT_ID(System.getenv("GROUP_ME_BOT_ID")),
    GROUP_ME_GROUP_ID(System.getenv("GROUP_ME_GROUP_ID")),
    GROUP_ME_ACCESS_TOKEN(System.getenv("GROUP_ME_ACCESS_TOKEN")),
    DISCORD_WEBHOOK_URL(System.getenv("DISCORD_WEBHOOK_URL")),
    SLACK_WEBHOOK_URL(System.getenv("SLACK_WEBHOOK_URL")),
    TIMEZONE(System.getenv("TIMEZONE")),
    RESTART_MESSAGE(System.getenv("RESTART_MESSAGE")),
    PORT(System.getenv("PORT")),
    JDBC_DATABASE_URL(System.getenv("JDBC_DATABASE_URL"));

    private final String value;

    EnvHandler(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
