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

package com.landonpatmore.yahoofantasybot.shared.utils.models

sealed class EnvVariable {
    sealed class Str(val variable: String, val optional: Boolean = false) : EnvVariable() {
        object YahooClientId : Str(System.getenv("YAHOO_CLIENT_ID") ?: "")
        object YahooClientSecret : Str(System.getenv("YAHOO_CLIENT_SECRET") ?: "")

        // Don't know if yahoo cares about the case, but looking at docs, they are lower case
        // It shouldn't matter, but knowing Yahoo, it does
        object YahooGameKey : Str(System.getenv("YAHOO_GAME_KEY")?.toLowerCase() ?: "")
        object YahooLeagueId : Str(System.getenv("YAHOO_LEAGUE_ID") ?: "")
        object GroupMeBotId : Str(System.getenv("GROUP_ME_BOT_ID") ?: "", true)
        object DiscordWebhookUrl : Str(System.getenv("DISCORD_WEBHOOK_URL") ?: "", true)
        object SlackWebhookUrl : Str(System.getenv("SLACK_WEBHOOK_URL") ?: "", true)
        object JdbcDatabaseUrl : Str(System.getenv("JDBC_DATABASE_URL") ?: "")
    }

    sealed class Integer(val variable: Int) : EnvVariable() {
        object Port : Integer(System.getenv("PORT")?.toIntOrNull() ?: -1)
    }
}