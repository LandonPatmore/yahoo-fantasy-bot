package bot.modules

import bot.bridges.*
import org.koin.dsl.module

val bridgesModule = module {
    single { CloseScoreUpdateBridge() }
    single { MatchUpBridge() }
    single { MessageBridge() }
    single { ScoreUpdateBridge() }
    single { StandingsBridge() }
    single { TransactionsBridge() }
}