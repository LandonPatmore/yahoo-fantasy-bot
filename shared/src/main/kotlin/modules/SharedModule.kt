package modules

import org.koin.dsl.module
import shared.EnvVariablesChecker
import shared.IDatabase
import shared.MockDatabase
import shared.Postgres

val sharedModule = module {
    single { EnvVariablesChecker() }
//    single { Postgres() as IDatabase }
    single { MockDatabase() as IDatabase }
}