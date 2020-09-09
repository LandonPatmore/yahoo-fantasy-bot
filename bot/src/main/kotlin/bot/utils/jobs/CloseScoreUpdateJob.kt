package bot.utils.jobs

import bot.bridges.CloseScoreUpdateBridge
import bot.utils.DataRetriever
import bot.utils.models.YahooApiRequest
import org.quartz.JobExecutionContext

class CloseScoreUpdateJob(
    private val dataRetriever: DataRetriever,
    private val closeScoreUpdateBridge: CloseScoreUpdateBridge
) : BaseJob(dataRetriever) {
    override val name = "CloseScore"

    override fun execute(context: JobExecutionContext?) {
        super.execute(context)

        val data = dataRetriever.yahooApiRequest(YahooApiRequest.TeamsData)
        closeScoreUpdateBridge.dataObserver.accept(data)
    }
}
