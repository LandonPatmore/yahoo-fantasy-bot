package bot.utils.jobs

import bot.bridges.MatchUpBridge
import bot.utils.DataRetriever
import bot.utils.models.YahooApiRequest
import org.quartz.Job
import org.quartz.JobExecutionContext

class MatchUpJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Match Up Update Job...")

        val data = DataRetriever.yahooApiRequest(YahooApiRequest.TeamsData)
        MatchUpBridge.dataObserver.accept(data)
    }
}
