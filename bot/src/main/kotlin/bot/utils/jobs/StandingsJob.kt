package bot.utils.jobs

import bot.bridges.StandingsBridge
import bot.utils.DataRetriever
import org.quartz.Job
import org.quartz.JobExecutionContext

class StandingsJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Standings Update Job...")

        val data = DataRetriever.getStandings()
        StandingsBridge.dataObserver.accept(data)
    }

}
