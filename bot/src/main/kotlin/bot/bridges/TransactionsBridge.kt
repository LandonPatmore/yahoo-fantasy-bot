package bot.bridges

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import org.jsoup.nodes.Document

class TransactionsBridge : Bridge<Pair<Long, Document>> {
    private val dataBridge = PublishRelay.create<Pair<Long, Document>>()

    override val consumer: Consumer<Pair<Long, Document>>
        get() = dataBridge

    override val eventStream: Observable<Pair<Long, Document>>
        get() = dataBridge
}
