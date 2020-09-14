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

package com.landonpatmore.yahoofantasybot.backend.models

data class Alerts(
    val alerts: List<Alert>
)

data class Alert(
    val type: Int, // TODO: Map these to actual alerts
    val hour: Int,
    val minute: Int,
    val startMonth: Int,
    val endMonth: Int,
    val dayOfWeek: Int,
    val timeZone: String
)

//jobRunner.createJob(MatchUpJob::class.java, "0 30 23 ? 9-1 THU *")
//jobRunner.createJob(StandingsJob::class.java, "0 30 16 ? 9-1 TUE *")
//jobRunner.createJob(ScoreUpdateJob::class.java, "0 55 3 ? 9-1 FRI *")
//jobRunner.createJob(ScoreUpdateJob::class.java, "0 00 17 ? 9-1 SUN *")
//jobRunner.createJob(ScoreUpdateJob::class.java, "0 00 20 ? 9-1 SUN *")
//jobRunner.createJob(ScoreUpdateJob::class.java, "0 00 0 ? 9-1 MON *")
//jobRunner.createJob(ScoreUpdateJob::class.java, "0 55 3 ? 9-1 MON *")
//jobRunner.createJob(ScoreUpdateJob::class.java, "0 55 3 ? 9-1 TUE *")