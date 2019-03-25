package com.topjohnwu.magisk.util

import com.skoumal.teanity.extensions.applySchedulers
import io.reactivex.*
import io.reactivex.disposables.Disposable
import timber.log.Timber

open class CompletableBuilder {
    internal var onErrorAction: (Throwable) -> Unit = { Timber.e(it) }
    internal var onCompleteAction: () -> Unit = {}

    fun onError(subscriber: (Throwable) -> Unit) {
        onErrorAction = subscriber
    }

    fun onComplete(subscriber: () -> Unit) {
        onCompleteAction = subscriber
    }
}

class ObserverBuilder<T> : CompletableBuilder() {

    internal var onNextAction: (T) -> Unit = {}

    fun onNext(subscriber: (T) -> Unit) {
        onNextAction = subscriber
    }

}

class SingleBuilder<T> {

    internal var onNextAction: (T) -> Unit = {}
    internal var onErrorAction: (Throwable) -> Unit = { Timber.e(it) }

    fun onSuccess(subscriber: (T) -> Unit) {
        onNextAction = subscriber
    }

    fun onError(subscriber: (Throwable) -> Unit) {
        onErrorAction = subscriber
    }

}

fun <T> Observable<T>.assign(builder: ObserverBuilder<T>.() -> Unit): Disposable {
    val result = ObserverBuilder<T>().apply(builder)
    return applySchedulers()
        .subscribe(result.onNextAction, result.onErrorAction, result.onCompleteAction)
}

fun <T> Flowable<T>.assign(builder: ObserverBuilder<T>.() -> Unit): Disposable {
    val result = ObserverBuilder<T>().apply(builder)
    return applySchedulers()
        .subscribe(result.onNextAction, result.onErrorAction, result.onCompleteAction)
}

fun <T> Maybe<T>.assign(builder: ObserverBuilder<T>.() -> Unit): Disposable {
    val result = ObserverBuilder<T>().apply(builder)
    return applySchedulers()
        .subscribe(result.onNextAction, result.onErrorAction, result.onCompleteAction)
}

fun <T> Single<T>.assign(builder: SingleBuilder<T>.() -> Unit): Disposable {
    val result = SingleBuilder<T>().apply(builder)
    return applySchedulers()
        .subscribe(result.onNextAction, result.onErrorAction)
}

fun Completable.assign(builder: CompletableBuilder.() -> Unit): Disposable {
    val result = CompletableBuilder().apply(builder)
    return applySchedulers()
        .subscribe(result.onCompleteAction, result.onErrorAction)
}