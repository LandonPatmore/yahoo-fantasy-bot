package bot.modules

import bot.messaging.*
import org.koin.dsl.module

val messagingModule = module {
    single { Discord() }
    single { Slack() }
    single { GroupMe() }
    single { listOf<IMessagingService>(get<Discord>(), get<Slack>(), get<GroupMe>()) }
}