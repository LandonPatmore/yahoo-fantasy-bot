package utils

import bridges.TransactionsBridge
import io.reactivex.Observable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object MessageSender {
    fun start() {
        val latch = CountDownLatch(1)
        Observable.interval(0, 15, TimeUnit.SECONDS)
            .subscribe {
                val event = DataRetriever.getTransactions()
                TransactionsBridge.dataObserver.onNext(event)
            }
        latch.await()
    }
}