package bot.utils.jobs

import bot.bridges.MessageBridge
import bot.bridges.CloseScoreUpdateBridge
import org.quartz.Job
import org.quartz.JobExecutionContext
import bot.messaging_services.Message
import bot.utils.DataRetriever

class CloseScoreUpdateJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Close Score Update Job...")

        val header = Message.Generic("📣<b>CLOSE SCORE ALERT</b> 🤞\\n━━━━━━━━━━━━━")
        MessageBridge.dataObserver.accept(header)

        val data = DataRetriever.getTeamsData()
        CloseScoreUpdateBridge.dataObserver.accept(data)
    }
}
