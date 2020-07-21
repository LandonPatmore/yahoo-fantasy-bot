package bot.utils.jobs

import bot.bridges.StandingsBridge
import bot.utils.DataRetriever
import bot.utils.models.YahooApiRequest
import org.quartz.Job
import org.quartz.JobExecutionContext

class StandingsJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Standings Update Job...")

        val data = DataRetriever.yahooApiRequest(YahooApiRequest.Standings)
        StandingsBridge.dataObserver.accept(data)
    }

}
