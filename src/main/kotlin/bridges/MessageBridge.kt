package bridges

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import types.Message

object MessageBridge : Bridge<Message> {
    private val dataBridge = PublishSubject.create<Message>()

    override val dataObserver: Observer<Message>
        get() = dataBridge

    override val dataObservable: Observable<Message>
        get() = dataBridge
}