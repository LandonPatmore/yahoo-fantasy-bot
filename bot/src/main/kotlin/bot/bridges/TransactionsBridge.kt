package bot.bridges

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import org.jsoup.nodes.Document

object TransactionsBridge : Bridge<Pair<Long, Document>> {
    private val dataBridge = PublishSubject.create<Pair<Long, Document>>()

    override val dataObserver: Observer<Pair<Long, Document>>
        get() = dataBridge

    override val dataObservable: Observable<Pair<Long, Document>>
        get() = dataBridge
}
