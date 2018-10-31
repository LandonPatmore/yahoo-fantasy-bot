package enums;

import utils.Props;

public enum GroupMeEnum implements EnumInterface {
    // URLS
    POST_URL("https://api.groupme.com/v3/bots/post"),
    GROUP_URL("https://api.groupme.com/v3/groups/"),

    // Authentication
    BOT_ID(Props.getGroupMeBotId()),
    GROUP_ID(Props.getGroupMeGroupId()),
    ACCESS_TOKEN(Props.getGroupMeAccessToken());

    private final String value;

    GroupMeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
