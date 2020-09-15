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

package shared.database

import com.github.scribejava.core.model.OAuth2AccessToken
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import shared.database.models.Alert
import shared.database.models.GameKey
import shared.database.models.LeagueId
import shared.database.tables.*

class Db(
    private val url: String,
    private val user: String = "test",
    private val password: String = "test"
) {

    init {
        connect()
        createTables()
    }

    private fun connect() {
        Database.connect(
            url,
            driver = "org.postgresql.Driver", user, password
        )
    }

    private fun createTables() {
        transaction {
            SchemaUtils.create(
                LatestTimeTable, StartupMessageTable, TokensTable,
                MessagingServicesTable, MessageTypeTable, AlertsTable
            )
        }
    }

    fun saveAlerts(alerts: List<Alert>) {
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
                    it[timeZone] = alert.timeZone
                }
            }
        }
    }

    fun saveGameKey(gameKey: GameKey) {
        transaction {
            GameKeyTable.insert {
                it[this.gameKey] = gameKey.key
            }
        }
    }

    fun saveLatestTime(time: Long) {
        transaction {
            dropTopRows(LatestTimeTable, LatestTimeTable.latestTime)

            LatestTimeTable.insert {
                it[latestTime] = time
            }
        }
    }

    fun saveLeagueId(leagueId: LeagueId) {
        transaction {
            LeagueIdTable.insert {
                it[this.leagueId] = leagueId.id
            }
        }
    }

    fun saveMessagingServices(services: shared.database.models.MessagingServices) {
        transaction {
            MessagingServicesTable.deleteAll()

            services.urls.forEach { service ->
                MessagingServicesTable.insert {
                    it[this.service] = service.service
                    it[url] = service.url
                }
            }
        }
    }

    fun startupMessageReceived() {
        transaction {
            StartupMessageTable.insert {
                it[received] = true
            }
        }
    }

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
                it[scope] = token.scope
            }
        }
    }

    fun wasStartupMessageReceived(): Boolean {
        return transaction {
            StartupMessageTable.selectAll().orderBy(
                StartupMessageTable.received to SortOrder.DESC
            ).limit(1).map {
                it[StartupMessageTable.received]
            }.first()
        }
    }

    fun getLatestTimeChecked(): Long {
        return transaction {
            LatestTimeTable.selectAll().orderBy(
                LatestTimeTable.latestTime to SortOrder.DESC
            ).limit(1).map {
                it[LatestTimeTable.latestTime]
            }.first()
        }
    }

    fun getLatestTokenData(): Pair<Long, OAuth2AccessToken> {
        return transaction {
            TokensTable.selectAll().orderBy(
                TokensTable.retrievedTime to SortOrder.DESC
            ).limit(1).map {
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
            }.first()
        }
    }

    fun getGameKeys(): List<String> {
        return transaction {
            GameKeyTable.selectAll().map {
                it[GameKeyTable.gameKey]
            }
        }
    }

    fun getLeagueIds(): List<String> {
        return transaction {
            LeagueIdTable.selectAll().map {
                it[LeagueIdTable.leagueId]
            }
        }
    }

    fun getMessageType(): Int {
        return transaction {
            MessageTypeTable.selectAll().map {
                it[MessageTypeTable.type]
            }.first()
        }
    }

    fun getAlerts(): List<Alert> {
        return transaction {
            AlertsTable.selectAll().map {
                Alert(
                    it[AlertsTable.type],
                    it[AlertsTable.hour],
                    it[AlertsTable.minute],
                    it[AlertsTable.startMonth],
                    it[AlertsTable.endMonth],
                    it[AlertsTable.dayOfWeek],
                    it[AlertsTable.timeZone]
                )
            }
        }
    }

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