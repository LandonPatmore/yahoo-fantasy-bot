package shared

import com.github.scribejava.core.model.OAuth2AccessToken

class MockDatabase : IDatabase {
    override fun startupMessageSent(): Boolean {
        return false
    }

    override fun latestTimeChecked(): Long {
        return 10000000
    }

    override fun latestTokenData(): Pair<Long, OAuth2AccessToken>? {
        return Pair(10000000, OAuth2AccessToken("TEST"))
    }

    override fun saveTokenData(token: OAuth2AccessToken) {
        println("Mocking - save token data")
    }

    override fun saveLastTimeChecked() {
        println("Mocking - save token last time checked")
    }

    override fun markStartupMessageReceived() {
        println("Mocking - save mark startup message received")
    }
}