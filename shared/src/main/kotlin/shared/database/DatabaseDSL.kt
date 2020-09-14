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

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import shared.database.tables.*

class DatabaseDSL {

    fun connect() {
        Database.connect(
            "jdbc:postgresql://localhost:5432/landon",
            driver = "org.postgresql.Driver"
        )
    }

    fun createTables() {
        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(
                LatestTime, StartupMessage, Tokens,
                MessagingServices, Alerts
            )
        }
    }

    fun insertLatestTime(time: Long) {
        transaction {
            addLogger(StdOutSqlLogger)
            LatestTime.insert {
                it[latestTime] = time
            }
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
}