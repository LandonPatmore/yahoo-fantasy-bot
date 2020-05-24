package bot.bridges

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import org.jsoup.nodes.Document

object CloseScoreUpdateBridge : Bridge<Document> {
    private val dataBridge = PublishRelay.create<Document>()

    override val dataObserver: Consumer<Document>
        get() = dataBridge

    override val dataObservable: Observable<Document>
        get() = dataBridge
}
