package utils

import bridges.CloseScoreUpdateBridge
import bridges.MatchUpBridge
import bridges.ScoreUpdateBridge
import bridges.StandingsBridge
import types.Task
import java.util.*
import java.util.concurrent.TimeUnit

private const val EVERY_AMOUNT_OF_DAYS = 7L

object UpdateCreator {
    fun createUpdate(updateName: String, hour: Int, minute: Int, day: Int, type: Task) {
        val timer = Timer()
        timer.scheduleAtFixedRate(
            createTask(updateName, type),
            createTime(
                hour,
                minute,
                day,
                TimeZone.getTimeZone("EST") // EST because all games are based on EST
            ),
            TimeUnit.MILLISECONDS.convert(EVERY_AMOUNT_OF_DAYS, TimeUnit.DAYS)
        )
    }

    private fun createTime(hour: Int, minute: Int, day: Int, timezone: TimeZone): Date {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, hour)
        today.set(Calendar.MINUTE, minute)
        today.set(Calendar.DAY_OF_WEEK, day)
        today.timeZone = timezone

        return today.time
    }

    private fun createTask(updateName: String, type: Task): TimerTask {
        return object : TimerTask() {
            override fun run() {
                println("$updateName running...")
                when (type) {
                    is Task.MatchUpUpdate -> {
                        val data = DataRetriever.getTeamsData()
                        MatchUpBridge.dataObserver.onNext(data)
                    }
                    is Task.ScoreUpdate -> {
                        val data = DataRetriever.getTeamsData()
                        ScoreUpdateBridge.dataObserver.onNext(data)
                    }
                    is Task.CloseScoreUpdate -> {
                        val data = DataRetriever.getTeamsData()
                        CloseScoreUpdateBridge.dataObserver.onNext(data)
                    }
                    is Task.StandingsUpdate -> {
                        val data = DataRetriever.getStandings()
                        StandingsBridge.dataObserver.onNext(data)
                    }
                }
            }
        }
    }
}