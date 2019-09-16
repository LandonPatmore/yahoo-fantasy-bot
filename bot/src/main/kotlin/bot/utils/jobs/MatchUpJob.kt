package bot.utils.jobs

import bot.bridges.MessageBridge
import bot.bridges.MatchUpBridge
import org.quartz.Job
import org.quartz.JobExecutionContext
import bot.messaging_services.Message
import bot.utils.DataRetriever

class MatchUpJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Match Up Update Job...")

        val header = Message.Generic("📣 <b>MATCH UP ALERT</b> 👯\\n━━━━━━━━━━━━")
        MessageBridge.dataObserver.onNext(header)

        val data = DataRetriever.getTeamsData()
        MatchUpBridge.dataObserver.onNext(data)
    }
}
