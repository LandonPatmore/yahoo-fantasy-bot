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

package com.landonpatmore.yahoofantasybot.bot.utils

import com.landonpatmore.yahoofantasybot.bot.utils.models.YahooApiRequest
import org.jsoup.nodes.Document

interface IDataRetriever {
    /**
     * Checks to see whether or not a token is expired.
     *
     * @param retrieved when the token was initially retrieved
     * @param expiresIn when the token expires
     * @return if the token is expired
     */
    fun isTokenExpired(retrieved: Long, expiresIn: Int): Boolean

    /**
     * Refreshes an expired token.
     */
    fun refreshExpiredToken()

    /**
     * Gets authentication token from DB if it exists.
     */
    fun getAuthenticationToken()

    /**
     * Grabs data requested from Yahoo.
     */
    fun grabData(url: String): Document

    /**
     * Retrieves the Yahoo Game key for the specified game.
     */
    fun retrieveGameKey(): String?

    /**
     * Makes a request out to Yahoo and returns data.
     */
    @Deprecated(
        "To be replaced soon with a generic object that the rest of the system can read, thus no reliance on the document object from jsoup",
        ReplaceWith(""),
        DeprecationLevel.WARNING
    )
    fun yahooApiRequest(yahooApiRequest: YahooApiRequest): Document

    /**
     * Gets transactions for league.
     */
    @Deprecated(
        "To be replaced soon with a generic object that the rest of the system can read, thus no reliance on the document object from jsoup",
        ReplaceWith(""),
        DeprecationLevel.WARNING
    )
    fun getTransactions(): Document

    /**
     * Gets standings for league.
     */
    @Deprecated(
        "To be replaced soon with a generic object that the rest of the system can read, thus no reliance on the document object from jsoup",
        ReplaceWith(""),
        DeprecationLevel.WARNING
    )
    fun getStandings(): Document

    /**
     * Gets teams data for league.
     */
    @Deprecated(
        "To be replaced soon with a generic object that the rest of the system can read, thus no reliance on the document object from jsoup",
        ReplaceWith(""),
        DeprecationLevel.WARNING
    )
    fun getTeamsData(): Document
}