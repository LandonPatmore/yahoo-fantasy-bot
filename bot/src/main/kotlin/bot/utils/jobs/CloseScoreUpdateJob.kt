package bot.utils.jobs

import bot.bridges.ScoreUpdateBridge
import org.quartz.Job
import org.quartz.JobExecutionContext
import bot.utils.DataRetriever

class CloseScoreUpdateJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Close Score Update Job...")
        val data = DataRetriever.getTeamsData()
        ScoreUpdateBridge.dataObserver.onNext(data)
    }
}
