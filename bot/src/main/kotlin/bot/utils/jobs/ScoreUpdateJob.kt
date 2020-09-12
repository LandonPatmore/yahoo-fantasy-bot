package bot.utils.jobs

import bot.bridges.ScoreUpdateBridge
import bot.utils.DataRetriever
import bot.utils.models.YahooApiRequest
import org.quartz.JobExecutionContext

class ScoreUpdateJob(
    private val dataRetriever: DataRetriever,
    private val scoreUpdateBridge: ScoreUpdateBridge
) :
    BaseJob() {
    override val name = "Score"

    override fun execute(context: JobExecutionContext?) {
        super.execute(context)

        val data = dataRetriever.yahooApiRequest(YahooApiRequest.TeamsData)
        scoreUpdateBridge.consumer.accept(data)
    }

}
