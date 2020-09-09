package bot

import bot.modules.bridgesModule
import bot.modules.messagingModule
import bot.modules.utilsModule
import bot.utils.Arbiter
import bot.utils.DataRetriever
import modules.sharedModule
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import shared.EnvVariablesChecker

class Bot : KoinComponent {
    val envVariablesChecker: EnvVariablesChecker by inject()
    val dataRetriever: DataRetriever by inject()
    val arbiter: Arbiter by inject()
}

fun main() {
    startKoin {
        modules(sharedModule, bridgesModule, messagingModule, utilsModule)
    }

    val bot = Bot()
//    bot.envVariablesChecker.check()
//    bot.dataRetriever.getAuthenticationToken()
    bot.arbiter.start()
}
