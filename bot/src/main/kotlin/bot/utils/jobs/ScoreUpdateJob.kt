package bot.utils.jobs

import bot.bridges.ScoreUpdateBridge
import bot.utils.DataRetriever
import bot.utils.models.YahooApiRequest
import org.quartz.Job
import org.quartz.JobExecutionContext

class ScoreUpdateJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Score Update Job...")

        val data = DataRetriever.yahooApiRequest(YahooApiRequest.TeamsData)
        ScoreUpdateBridge.dataObserver.accept(data)
    }

}
