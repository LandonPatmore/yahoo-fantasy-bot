package shared

import com.github.scribejava.core.model.OAuth2AccessToken

interface IDatabase {
    /**
     * Checks to see if the startup message was sent to the users.
     *
     * @return whether or not the startup message was sent
     */
    fun startupMessageSent(): Boolean

    /**
     * Gets the latest time checked.
     *
     * @return long of the time
     */
    fun latestTimeChecked(): Long

    /**
     * Gets the latest token data
     *
     * @return token data
     */
    fun latestTokenData(): Pair<Long, OAuth2AccessToken>?

    /**
     * Saves the Yahoo token data to the DB.
     *
     * @param token token to be saved
     */
    fun saveTokenData(token: OAuth2AccessToken)

    /**
     * Saves the last time data was checked.
     */
    fun saveLastTimeChecked()

    /**
     * Marks that the start up message has been received by the users.  This is so that the message is not sent every-time the application is started.
     */
    fun markStartupMessageReceived()
}