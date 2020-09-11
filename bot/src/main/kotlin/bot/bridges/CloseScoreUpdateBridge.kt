package bot.bridges

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import org.jsoup.nodes.Document

class CloseScoreUpdateBridge : Bridge<Document> {
    private val dataBridge = PublishRelay.create<Document>()

    override val consumer: Consumer<Document>
        get() = dataBridge

    override val eventStream: Observable<Document>
        get() = dataBridge
}
