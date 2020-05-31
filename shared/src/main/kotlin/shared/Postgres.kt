package shared

import com.github.scribejava.core.model.OAuth2AccessToken
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


object Postgres {
    private var connection: Connection? = null

    /**
     * Checks to see if the startup message was sent to the users.
     *
     * @return whether or not the startup message was sent
     */
    val startupMessageSent: Boolean
        get() {
            return try {
                getConnection()

                val statement = connection!!.createStatement()
                val row =
                    statement.executeQuery("SELECT * FROM start_up_message_received ORDER BY \"was_received\" DESC LIMIT 1")

                if (row.next()) {
                    row.getBoolean("was_received")
                } else false

            } catch (e: SQLException) {
                println(e.localizedMessage)
                false
            }
        }

    /**
     * Gets the latest time checked.
     *
     * @return long of the time
     */
    val latestTimeChecked: Long
        get() {
            dropTopRows("latest_time", "latest_time")
            return try {
                getConnection()

                val statement = connection!!.createStatement()
                val row = statement.executeQuery("SELECT * FROM latest_time ORDER BY \"latest_time\" DESC LIMIT 1")

                if (row.next()) {
                    row.getLong("latest_time")
                } else System.currentTimeMillis() / 1000

            } catch (e: SQLException) {
                println(e.localizedMessage)
                System.currentTimeMillis() / 1000
            }
        }

    /**
     * Gets the latest token data
     *
     * @return token data
     */
    val latestTokenData: Pair<Long, OAuth2AccessToken>?
        get() {
            dropTopRows("tokens", "yahooTokenRetrievedTime")

            try {
                getConnection()

                val statement = connection!!.createStatement()
                val row =
                    statement.executeQuery("SELECT * FROM tokens ORDER BY \"yahooTokenRetrievedTime\" DESC LIMIT 1")

                if (row.next()) {
                    val refreshToken = row.getString("yahooRefreshToken")
                    val retrievedTime = row.getLong("yahooTokenRetrievedTime")
                    val rawResponse = row.getString("yahooTokenRawResponse")
                    val tokenType = row.getString("yahooTokenType")
                    val accessToken = row.getString("yahooAccessToken")
                    val expiresIn = row.getLong("yahooTokenExpireTime")
                    val scope = row.getString("yahooTokenScope")

                    return Pair(
                        retrievedTime,
                        OAuth2AccessToken(
                            accessToken,
                            tokenType,
                            expiresIn.toInt(),
                            refreshToken,
                            scope,
                            rawResponse
                        )
                    )
                }
                return null
            } catch (e: SQLException) {
                println(e.localizedMessage)
                return null
            }
        }

    /**
     * Gets the connection to the DB.  Loops indefinitely.
     *
     * @return connection to DB
     */
    private fun getConnection(): Connection? {

        while (connection == null) {
            try {
                println("Connection does not exist to database.  Creating...")

                connection = DriverManager.getConnection(EnvVariable.Str.JdbcDatabaseUrl.variable)

                println("Connection established to database.")

                return connection
            } catch (e: SQLException) {
                println(e.localizedMessage)
                try {
                    Thread.sleep(5000)
                } catch (e1: InterruptedException) {
                    println(e.localizedMessage)
                }
            }
        }

        return connection
    }

    /**
     * Saves the Yahoo token data to the DB.
     *
     * @param token token to be saved
     */
    fun saveTokenData(token: OAuth2AccessToken) {
        try {
            getConnection()

            println("Attempting to save token data...")

            val refreshToken = token.refreshToken
            val retrievedTime = System.currentTimeMillis().toString()
            val rawResponse = token.rawResponse
            val tokenType = token.tokenType
            val accessToken = token.accessToken
            val expiresIn = token.expiresIn!!.toString()
            val scope = token.scope

            val statement = connection!!.createStatement()
            val sql =
                "INSERT INTO tokens (\"yahooRefreshToken\",\"yahooTokenRetrievedTime\",\"yahooTokenRawResponse\",\"yahooTokenType\",\"yahooAccessToken\", \"yahooTokenExpireTime\", \"yahooTokenScope\") VALUES (\'$refreshToken\',\'$retrievedTime\',\'$rawResponse\',\'$tokenType\',\'$accessToken\',\'$expiresIn\',$scope)"

            statement.executeUpdate(sql)

            println("Token data has been saved.")
        } catch (e: SQLException) {
            println(e.localizedMessage)
        }
    }

    /**
     * Saves the last time data was checked.
     */
    fun saveLastTimeChecked() {
        try {
            getConnection()

            println("Attempting to save last time checked...${System.currentTimeMillis() / 1000}")

            val statement = connection!!.createStatement()
            val sql =
                "INSERT INTO latest_time (\"latest_time\")" + " VALUES (\'" + System.currentTimeMillis() / 1000 + "\')"

            statement.executeUpdate(sql)

            println("Latest time has been saved.")
        } catch (e: SQLException) {
            println(e.localizedMessage)
        }

    }

    /**
     * Marks that the start up message has been received by the users.  This is so that the message is not sent every-time the application is started.
     */
    fun markStartupMessageReceived() {
        try {
            getConnection()

            println("Marking startup message sent...")

            val statement = connection!!.createStatement()
            val sql = "INSERT INTO start_up_message_received (\"was_received\")" + " VALUES (\'" + true + "\')"

            statement.executeUpdate(sql)

            println("Startup message marked sent.")
        } catch (e: SQLException) {
            println(e.localizedMessage)
        }
    }

    /**
     * Drops the top rows of a given table.
     *
     * @param tableName table name
     * @param orderBy   the column to order by
     */
    private fun dropTopRows(tableName: String, orderBy: String) {
        try {
            getConnection()
            val statement = connection!!.createStatement()
            val row = statement.executeQuery("SELECT COUNT(*) FROM $tableName")

            if (row.next()) {
                val count = row.getInt(1)

                if (count > 20) {
                    println("More than 20 entries in the $tableName table.  Removing top 20.")
                    statement.execute(
                        "DELETE\n" +
                                "FROM " + tableName + "\n" +
                                "WHERE ctid IN (\n" +
                                "        SELECT ctid\n" +
                                "        FROM " + tableName + "\n" +
                                "        ORDER BY \"" + orderBy + "\" limit 20\n" +
                                "        )"
                    )
                }
            }

        } catch (e: SQLException) {
            println(e.localizedMessage)
        }

    }
}
