package bot.utils.jobs

import bot.bridges.MatchUpBridge
import bot.utils.DataRetriever
import org.quartz.Job
import org.quartz.JobExecutionContext

class MatchUpJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Match Up Update Job...")

        val data = DataRetriever.getTeamsData()
        MatchUpBridge.dataObserver.accept(data)
    }
}
