package bot.utils.jobs

import bot.utils.DataRetriever
import org.quartz.Job
import org.quartz.JobExecutionContext

abstract class BaseJob(private val dataRetriever: DataRetriever) : Job {
    protected abstract val name: String

    override fun execute(context: JobExecutionContext?) {
        println("$name update job running...")
    }
}