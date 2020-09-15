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
import shared.database.models.GameKey
import shared.database.models.LeagueId
import shared.database.tables.*

class DatabaseDSL {

    init {
        connect()
        createTables()
    }

    private fun connect() {
        Database.connect(
            "jdbc:postgresql://localhost:5432/landon",
            driver = "org.postgresql.Driver"
        )
    }

    private fun createTables() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(
                LatestTime, StartupMessage, Tokens,
                MessagingServices, Alerts
            )
        }
    }

    fun insertAlert(alerts: shared.database.models.Alerts) {
        alerts.alerts.forEach { alert ->
            transaction {
                addLogger(StdOutSqlLogger)
                Alerts.insert {
                    it[type] = alert.type
                    it[cronExpression] = "0 ${alert.minute} ${alert.hour} ? " +
                            "${alert.startMonth}-${alert.endMonth} ${
                                alert
                                    .dayOfWeek
                            } *"
                }
            }
        }
    }

    fun insertGameKey(gameKey: GameKey) {
        transaction {
            addLogger(StdOutSqlLogger)
            shared.database.tables.GameKey.insert {
                it[this.gameKey] = gameKey.key
            }
        }
    }

    fun insertLatestTime(time: Long) {
        dropTopRows(Drop.LatestTimes)

        transaction {
            addLogger(StdOutSqlLogger)
            LatestTime.insert {
                it[latestTime] = time
            }
        }
    }

    fun insertLeagueId(leagueId: LeagueId) {
        transaction {
            addLogger(StdOutSqlLogger)
            shared.database.tables.LeagueId.insert {
                it[this.leagueId] = leagueId.id
            }
        }
    }

    fun insertMessagingServices(services: shared.database.models.MessagingServices) {
        transaction {
            addLogger(StdOutSqlLogger)
            MessagingServices.deleteAll()
        }

        services.urls.forEach { service ->
            transaction {
                addLogger(StdOutSqlLogger)
                MessagingServices.insert {
                    it[this.service] = service.service
                    it[url] = service.url
                }
            }
        }
    }

    fun insertStartupMessage() {
        transaction {
            addLogger(StdOutSqlLogger)
            transaction {
                StartupMessage.insert {
                    it[received] = true
                }
            }
        }
    }

    fun insertToken(token: OAuth2AccessToken) {
        dropTopRows(Drop.Tokens)

        transaction {
            addLogger(StdOutSqlLogger)
            transaction {
                Tokens.insert {
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
    }

    fun dropTopRows(drop: Drop) {
        val count = transaction {
            addLogger(StdOutSqlLogger)
            when (drop) {
                Drop.Tokens -> Tokens.selectAll().count()
                Drop.LatestTimes -> LatestTime.selectAll().count()
            }
        }

        if (count > 20) {
            transaction {
                addLogger(StdOutSqlLogger)
                when (drop) {
                    Drop.Tokens -> {
                        val tokens = Tokens.selectAll().orderBy(
                            Tokens.retrievedTime
                        ).limit(20).map {
                            it[Tokens.retrievedTime]
                        }
                        Tokens.deleteWhere {
                            Tokens.retrievedTime inList tokens
                        }
                    }
                    Drop.LatestTimes -> {
                        val latestTimes = LatestTime.selectAll().orderBy(
                            LatestTime.latestTime
                        ).limit(20).map {
                            it[LatestTime.latestTime]
                        }
                        LatestTime.deleteWhere {
                            LatestTime.latestTime inList latestTimes
                        }
                    }
                }
            }
        }
    }

    companion object {
        sealed class Drop {
            object Tokens : Drop()
            object LatestTimes : Drop()
        }
    }
}