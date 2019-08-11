import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import org.jsoup.nodes.Document

object DataBridge {
    private val dataBridge = BehaviorSubject.create<Document>()

    val dataObserver: Observer<Document> = dataBridge

    val dataObservable: Observable<Document> = dataBridge
}