package shared;

import com.github.scribejava.core.model.OAuth2AccessToken;

public class PostgresToken {
    private final OAuth2AccessToken token;
    private final Long retrievedTime;

    public PostgresToken(OAuth2AccessToken token, Long retrievedTime) {
        this.token = token;
        this.retrievedTime = retrievedTime;
    }

    public OAuth2AccessToken getToken() {
        return token;
    }

    public long getRetrievedTime() {
        return retrievedTime;
    }
}
