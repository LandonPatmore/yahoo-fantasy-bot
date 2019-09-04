package utils.jobs

import bridges.ScoreUpdateBridge
import org.quartz.Job
import org.quartz.JobExecutionContext
import utils.DataRetriever

class ScoreUpdateJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Score Update Job...")
        val data = DataRetriever.getTeamsData()
        ScoreUpdateBridge.dataObserver.onNext(data)
    }

}