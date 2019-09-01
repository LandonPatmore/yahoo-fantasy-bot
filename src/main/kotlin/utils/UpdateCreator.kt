package utils

import java.util.*
import java.util.concurrent.TimeUnit

object UpdateCreator {
    fun createUpdate(updateName: String, hour: Int, minute: Int, day: Int, updateFunction: () -> Unit) {
        val timer = Timer()
        timer.schedule(
            createTask(updateName, updateFunction),
            createTime(
                19,
                30,
                Calendar.THURSDAY,
                TimeZone.getTimeZone("EST") // EST because all games are based on EST
            ),
            TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS)
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

    private fun createTask(updateName: String, updateFunction: () -> Unit): TimerTask {
        return object : TimerTask() {
            override fun run() {
                println("$updateName running...")
                updateFunction()
            }
        }
    }
}