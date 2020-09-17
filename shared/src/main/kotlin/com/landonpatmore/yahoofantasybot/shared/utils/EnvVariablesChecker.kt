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

package com.landonpatmore.yahoofantasybot.shared.utils

import com.landonpatmore.yahoofantasybot.shared.utils.models.EnvVariable
import com.landonpatmore.yahoofantasybot.shared.utils.models.EnvVariableException
import com.landonpatmore.yahoofantasybot.shared.utils.models.GameKeyException

class EnvVariablesChecker {
    fun check() {
        checkString(EnvVariable.Str.YahooClientId)
        checkString(EnvVariable.Str.YahooClientSecret)
        checkGameKey(EnvVariable.Str.YahooGameKey)
        checkString(EnvVariable.Str.YahooLeagueId)
        checkString(EnvVariable.Str.GroupMeBotId)
        checkString(EnvVariable.Str.DiscordWebhookUrl)
        checkString(EnvVariable.Str.SlackWebhookUrl)
        checkString(EnvVariable.Str.JdbcDatabaseUrl)
        checkInt(EnvVariable.Integer.Port)
        // No need to check booleans as they are false if they don't exist or malformed
    }

    private fun checkGameKey(env: EnvVariable.Str) {
        checkString(env)

        when (env.variable.toLowerCase()) {
            "nfl", "nba", "mlb" -> return
            else -> throw GameKeyException(env)
        }
    }

    private fun checkString(env: EnvVariable.Str) {
        if (!env.optional && env.variable.trim().isEmpty()) {
            throw EnvVariableException(env)
        }
    }

    private fun checkInt(env: EnvVariable.Integer) {
        if (env.variable == -1) {
            throw EnvVariableException(env)
        }
    }
}