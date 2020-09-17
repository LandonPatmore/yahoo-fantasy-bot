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

package com.landonpatmore.yahoofantasybot.bot

import com.landonpatmore.yahoofantasybot.bot.utils.Arbiter
import com.landonpatmore.yahoofantasybot.bot.utils.DataRetriever
import com.landonpatmore.yahoofantasybot.bot.utils.IDataRetriever
import com.landonpatmore.yahoofantasybot.shared.utils.EnvVariablesChecker
import org.koin.core.KoinComponent
import org.koin.core.inject

class Bot : KoinComponent {
    val dataRetriever: IDataRetriever by inject()
    val envVariablesChecker: EnvVariablesChecker by inject()
    val arbiter: Arbiter by inject()
}
