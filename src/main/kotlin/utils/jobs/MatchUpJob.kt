package utils.jobs

import bridges.MatchUpBridge
import org.quartz.Job
import org.quartz.JobExecutionContext
import utils.DataRetriever

class MatchUpJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Match Up Update Job...")
        val data = DataRetriever.getTeamsData()
        MatchUpBridge.dataObserver.onNext(data)
    }
}