package bot.modules

import bot.messaging.MockMessageService
import bot.utils.Arbiter
import bot.utils.IDataRetriever
import bot.utils.DataRetriever
import bot.utils.JobRunner
import org.koin.dsl.module

val utilsModule = module {
    single { JobRunner() }
    single { DataRetriever(get()) as IDataRetriever }
    single { Arbiter(get(), get(), get(), get(), get(), get(), get(), get(), get(), listOf(get<MockMessageService>())) }
}