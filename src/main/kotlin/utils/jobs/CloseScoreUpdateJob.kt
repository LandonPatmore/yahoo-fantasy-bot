package utils.jobs

import bridges.ScoreUpdateBridge
import org.quartz.Job
import org.quartz.JobExecutionContext
import utils.DataRetriever

class CloseScoreUpdateJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Close Score Update Job...")
        val data = DataRetriever.getTeamsData()
        ScoreUpdateBridge.dataObserver.onNext(data)
    }
}