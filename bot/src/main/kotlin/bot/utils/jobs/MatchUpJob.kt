package bot.utils.jobs

import bot.bridges.MatchUpBridge
import bot.utils.DataRetriever
import bot.utils.models.YahooApiRequest
import org.quartz.JobExecutionContext

class MatchUpJob(private val dataRetriever: DataRetriever, private val matchUpBridge: MatchUpBridge) :
    BaseJob(dataRetriever) {
    override val name = "MatchUp"

    override fun execute(context: JobExecutionContext?) {
        super.execute(context)

        val data = dataRetriever.yahooApiRequest(YahooApiRequest.TeamsData)
        matchUpBridge.dataObserver.accept(data)
    }
}
