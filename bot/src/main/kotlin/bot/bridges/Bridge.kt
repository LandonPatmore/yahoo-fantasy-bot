package bot.bridges

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer

interface Bridge<T> {
    val consumer: Consumer<T>

    val eventStream: Observable<T>
}
