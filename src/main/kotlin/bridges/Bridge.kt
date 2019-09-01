package bridges

import io.reactivex.Observable
import io.reactivex.Observer
import org.jsoup.nodes.Document

interface Bridge {
    val dataObserver: Observer<Document>

    val dataObservable: Observable<Document>

}