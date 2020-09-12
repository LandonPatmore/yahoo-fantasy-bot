package bot.modules

import bot.messaging.Discord
import bot.messaging.GroupMe
import bot.messaging.IMessagingService
import bot.messaging.Slack
import org.koin.dsl.module

val messagingModule = module {
    single { Discord() }
    single { Slack() }
    single { GroupMe() }
    single {
        listOf<IMessagingService>(
            get<Discord>(),
            get<Slack>(),
            get<GroupMe>()
        )
    }
}