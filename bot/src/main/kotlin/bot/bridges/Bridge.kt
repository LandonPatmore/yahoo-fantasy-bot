package bot.bridges

import io.reactivex.Observable
import io.reactivex.Observer

interface Bridge<T> {
    val dataObserver: Observer<T>

    val dataObservable: Observable<T>

}
