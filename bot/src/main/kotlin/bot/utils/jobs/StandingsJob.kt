package bot.utils.jobs

import bot.bridges.StandingsBridge
import org.quartz.Job
import org.quartz.JobExecutionContext
import bot.utils.DataRetriever

class StandingsJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Standings Update Job...")
        val data = DataRetriever.getStandings()
        StandingsBridge.dataObserver.onNext(data)
    }

}
