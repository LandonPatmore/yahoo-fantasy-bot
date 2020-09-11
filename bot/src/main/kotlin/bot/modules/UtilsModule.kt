package bot.modules

import bot.utils.*
import org.koin.dsl.module

val utilsModule = module {
    single { JobRunner() }
    single { DataRetriever(get()) as IDataRetriever }
    single { Arbiter(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}