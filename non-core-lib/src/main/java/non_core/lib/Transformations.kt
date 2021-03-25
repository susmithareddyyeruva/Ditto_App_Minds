package non_core.lib

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer

fun <T> Single<T>.whileSubscribed(block: (Boolean) -> Unit): Single<T> {
    return compose(whileSubscribedSingleTransformer(block))
}

fun <T> Observable<T>.whileSubscribed(block: (Boolean) -> Unit): Observable<T> {
    return compose(whileSubscribedObservableTransformer(block))
}

private fun <T> whileSubscribedSingleTransformer(
    block: (Boolean) -> Unit
): SingleTransformer<T, T> {
    return SingleTransformer { upstream ->
        upstream.doOnSubscribe { block.invoke(true) }
            .doOnSuccess { block.invoke(false) }
            .doOnError { block.invoke(false) }
            .doOnDispose { block.invoke(false) }
    }
}

private fun <T> whileSubscribedObservableTransformer(
    block: (Boolean) -> Unit
): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream ->
        upstream.doOnSubscribe { block.invoke(true) }
            .doOnError { block.invoke(false) }
            .doOnComplete { block.invoke(false) }
            .doOnDispose { block.invoke(false) }
    }
}