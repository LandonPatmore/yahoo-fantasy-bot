package enums;

import utils.Props;

public enum YahooEnum implements EnumInterface {
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
