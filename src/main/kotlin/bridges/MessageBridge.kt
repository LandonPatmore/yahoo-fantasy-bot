package bridges

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import messaging_services.Message

object MessageBridge : Bridge<Message> {
    private val dataBridge = PublishSubject.create<Message>()

    override val dataObserver: Observer<Message>
        get() = dataBridge

    override val dataObservable: Observable<Message>
        get() = dataBridge
}