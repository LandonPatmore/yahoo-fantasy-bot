package utils

import java.util.*

object UpdateCreator {
    fun createTime(hour: Int, minute: Int, day: Int, timezone: TimeZone): Date {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, hour)
        today.set(Calendar.MINUTE, minute)
        today.set(Calendar.DAY_OF_WEEK, day)
        today.timeZone = timezone

        return today.time
    }

    fun createUpdate(updateName: String, updateFunction: () -> Unit): TimerTask {
        return object : TimerTask() {
            override fun run() {
                println("$updateName running...")
                updateFunction()
            }
        }
    }
}