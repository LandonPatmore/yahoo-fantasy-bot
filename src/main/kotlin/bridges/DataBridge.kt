package bridges

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import org.jsoup.nodes.Document

object DataBridge {
    private val dataBridge = PublishSubject.create<Document>()

    val dataObserver: Observer<Document> = dataBridge

    val dataObservable: Observable<Document> = dataBridge
}