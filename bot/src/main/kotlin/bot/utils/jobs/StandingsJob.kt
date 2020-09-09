package bot.utils.jobs

import bot.bridges.StandingsBridge
import bot.utils.DataRetriever
import bot.utils.models.YahooApiRequest
import org.quartz.JobExecutionContext

class StandingsJob(private val dataRetriever: DataRetriever, private val standingsBridge: StandingsBridge) :
    BaseJob() {
    override val name = "Standings"

    override fun execute(context: JobExecutionContext?) {
        super.execute(context)

        val data = dataRetriever.yahooApiRequest(YahooApiRequest.Standings)
        standingsBridge.dataObserver.accept(data)
    }

}
