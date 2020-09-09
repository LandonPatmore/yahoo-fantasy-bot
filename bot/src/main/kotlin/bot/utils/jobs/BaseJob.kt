package bot.utils.jobs

import org.quartz.Job
import org.quartz.JobExecutionContext

abstract class BaseJob() : Job {
    protected abstract val name: String

    override fun execute(context: JobExecutionContext?) {
        println("$name update job running...")
    }
}