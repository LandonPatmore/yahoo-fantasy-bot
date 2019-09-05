package bot.utils.jobs

import bot.bridges.ScoreUpdateBridge
import org.quartz.Job
import org.quartz.JobExecutionContext
import bot.utils.DataRetriever

class ScoreUpdateJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Score Update Job...")
        val data = DataRetriever.getTeamsData()
        ScoreUpdateBridge.dataObserver.onNext(data)
    }

}
