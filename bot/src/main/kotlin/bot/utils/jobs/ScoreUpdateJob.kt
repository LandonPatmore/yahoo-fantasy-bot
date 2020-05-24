package bot.utils.jobs

import bot.bridges.MessageBridge
import bot.bridges.ScoreUpdateBridge
import org.quartz.Job
import org.quartz.JobExecutionContext
import bot.messaging_services.Message
import bot.utils.DataRetriever

class ScoreUpdateJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Score Update Job...")

        val header = Message.Generic("📣<b>SCORE ALERT</b> 💯\\n━━━━━━━━━━")
        MessageBridge.dataObserver.accept(header)

        val data = DataRetriever.getTeamsData()
        ScoreUpdateBridge.dataObserver.accept(data)
    }

}
