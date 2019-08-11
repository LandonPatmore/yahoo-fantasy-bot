import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject

object MessageBridge {
    private val dataBridge = PublishSubject.create<String>()

    val dataObserver: Observer<String> = dataBridge

    val dataObservable: Observable<String> = dataBridge
}