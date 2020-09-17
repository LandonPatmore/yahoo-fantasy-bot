/*
 * MIT License
 *
 * Copyright (c) 2020 Landon Patmore
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.landonpatmore.yahoofantasybot.shared.database

import com.github.scribejava.core.model.OAuth2AccessToken
import com.landonpatmore.yahoofantasybot.shared.database.models.*
import com.landonpatmore.yahoofantasybot.shared.database.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class Db(
    private val url: String
) {

    init {
        connect()
        createTables()
    }

    /**
     * Connects to the specified database.
     */
    private fun connect() {
        Database.connect(
            url,
            driver = "org.postgresql.Driver"
        )
    }

    /**
     * Creates tables if they do not exist.
     */
    private fun createTables() {
        transaction {
            SchemaUtils.create(
                LatestTimesTable, StartupMessageTable, TokensTable,
                MessagingServicesTable, MessageTypeTable, AlertsTable,
                LeaguesTable
            )
        }
    }

    /**
     * Saves custom alerts.
     */
    fun saveAlerts(alerts: Array<Alert>) {
        transaction {
            AlertsTable.deleteAll()

            alerts.forEach { alert ->
                AlertsTable.insert {
                    it[type] = alert.type
                    it[hour] = alert.hour
                    it[minute] = alert.minute
                    it[startMonth] = alert.startMonth
                    it[endMonth] = alert.endMonth
                    it[dayOfWeek] = alert.dayOfWeek
//                    it[timeZone] = alert.timeZone
                }
            }
        }
    }

    /**
     * Saves the league information.
     */
    fun saveLeagues(leagues: Array<League>) {
        transaction {
            LeaguesTable.deleteAll()

            leagues.forEach { league ->
                LeaguesTable.insert {
                    it[leagueId] = league.leagueId
                    it[gameKey] = league.gameKey
                }
            }
        }
    }

    /**
     * Saves the latest time the bot checked the Yahoo servers for data.
     */
    fun saveLatestTimeChecked(time: Long) {
        transaction {
            dropTopRows(LatestTimesTable, LatestTimesTable.latestTime)

            LatestTimesTable.insert {
                it[latestTime] = time
            }
        }
    }

    /**
     * Saves the custom messaging service information.
     */
    fun saveMessagingServices(services: Array<MessagingService>) {
        transaction {
            MessagingServicesTable.deleteAll()

            services.forEach { service ->
                MessagingServicesTable.insert {
                    it[this.service] = service.service
                    it[url] = service.url
                }
            }
        }
    }

    /**
     * Saves the start up message was received.
     */
    fun startupMessageReceived() {
        transaction {
            StartupMessageTable.insert {
                it[received] = true
            }
        }
    }

    /**
     * Saves the OAuth token that Yahoo provides.
     */
    fun saveToken(token: OAuth2AccessToken) {
        transaction {
            dropTopRows(TokensTable, TokensTable.retrievedTime)

            TokensTable.insert {
                it[refreshToken] = token.refreshToken
                it[retrievedTime] = System.currentTimeMillis()
                it[rawResponse] = token.rawResponse
                it[type] = token.tokenType
                it[accessToken] = token.accessToken
                it[expireTime] = token.expiresIn
                it[scope] = token.scope ?: ""
            }
        }
    }

    fun saveMessageType(messageType: MessageType) {
        transaction {
            MessageTypeTable.deleteAll()

            MessageTypeTable.insert {
                it[type] = messageType.type
            }
        }
    }

    /**
     * Gets if start up message was received.
     */
    fun wasStartupMessageReceived(): Boolean {
        return transaction {
            StartupMessageTable.selectAll().firstOrNull()?.let {
                it[StartupMessageTable.received]
            } ?: false
        }
    }

    /**
     * Gets the latest time Yahoo servers were checked for data.
     */
    fun getLatestTimeChecked(): LatestTime {
        return transaction {
            LatestTimesTable.selectAll().orderBy(
                LatestTimesTable.latestTime to SortOrder.DESC
            ).firstOrNull()?.let {
                LatestTime(it[LatestTimesTable.latestTime])
            } ?: LatestTime(-1)
        }
    }

    /**
     * Gets the latest token OAuth token data.
     */
    fun getLatestTokenData(): Pair<Long, OAuth2AccessToken>? {
        return transaction {
            TokensTable.selectAll().orderBy(
                TokensTable.retrievedTime to SortOrder.DESC
            ).firstOrNull()?.let {
                Pair(
                    it[TokensTable.retrievedTime],
                    OAuth2AccessToken(
                        it[TokensTable.accessToken],
                        it[TokensTable.type],
                        it[TokensTable.expireTime].toInt(),
                        it[TokensTable.refreshToken],
                        it[TokensTable.scope],
                        it[TokensTable.rawResponse]
                    )
                )
            }
        }
    }

    /**
     * Gets all league information.
     */
    fun getLeagues(): List<League> {
        return transaction {
            LeaguesTable.selectAll().map {
                League(
                    it[LeaguesTable.leagueId],
                    it[LeaguesTable.gameKey]
                )
            }
        }
    }

    /**
     * Gets the message type the boy sends to messaging services.
     */
    fun getMessageType(): MessageType {
        return transaction {
            MessageTypeTable.selectAll().firstOrNull()?.let {
                MessageType(it[MessageTypeTable.type])
            } ?: MessageType(-1)
        }
    }

    /**
     * Gets custom alerts.
     */
    fun getAlerts(): List<Alert> {
        return transaction {
            AlertsTable.selectAll().map {
                Alert(
                    it[AlertsTable.type],
                    it[AlertsTable.hour],
                    it[AlertsTable.minute],
                    it[AlertsTable.startMonth],
                    it[AlertsTable.endMonth],
                    it[AlertsTable.dayOfWeek]
//                    it[AlertsTable.timeZone]
                )
            }
        }
    }

    /**
     * Gets messaging services information.
     */
    fun getMessagingServices(): List<MessagingService> {
        return transaction {
            MessagingServicesTable.selectAll().map {
                MessagingService(
                    it[MessagingServicesTable.service],
                    it[MessagingServicesTable.url]
                )
            }
        }
    }

    /**
     * Drops the top rows from the table.  The reason for this is so reduce
     * table size for space, as well as heroku has a hard limit on the amount
     * of rows across all tables.  This makes sure we never reach that limit.
     */
    private fun <T> dropTopRows(table: Table, column: Column<T>) {
        val count = table.selectAll().count()

        if (count >= 20) {
            val list = table.selectAll()
                .orderBy(column)
                .limit(20)
                .map { it[column] }

            table.deleteWhere { column inList list }
        }
    }
}