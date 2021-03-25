package core

import io.reactivex.Single
import io.reactivex.SingleTransformer

fun <T> Single<T>.whileSubscribed(block: (Boolean) -> Unit): Single<T> {
    return compose(whileSubscribedSingleTransformer(block))
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