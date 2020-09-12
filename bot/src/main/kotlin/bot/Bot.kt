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

package bot

import bot.modules.bridgesModule
import bot.modules.messagingModule
import bot.modules.utilsModule
import bot.utils.Arbiter
import bot.utils.DataRetriever
import modules.sharedModule
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import shared.EnvVariablesChecker

class Bot : KoinComponent {
    val envVariablesChecker: EnvVariablesChecker by inject()
    val dataRetriever: DataRetriever by inject()
    val arbiter: Arbiter by inject()
}

fun main() {
    startKoin {
        modules(sharedModule, bridgesModule, messagingModule, utilsModule)
    }

    val bot = Bot()
//    bot.envVariablesChecker.check()
//    bot.dataRetriever.getAuthenticationToken()
    bot.arbiter.start()
}
