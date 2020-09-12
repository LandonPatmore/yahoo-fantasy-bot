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