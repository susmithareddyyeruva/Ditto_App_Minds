package non_core.lib

import io.reactivex.Observable
import io.reactivex.Single

/**
 * A generic mechanism of providing access to an arbitrary entity of type [T]. For convenience,
 * this access is divided into three different modes:
 *
 * 1) Continuous observing of all changes - [stream]
 * 2) Getting the most recent value wrapped in [Single] - [single]
 * 3) Getting the most recent value in plain format - [value]
 */
interface Stream<T> {

    /**
     * Subscribe to the returned [Observable] to receive notifications every time when observing
     * value changes.
     *
     * **NOTE:** in most situations, implementations are supposed to provide continuous emission
     * of events instead of instant completion after the very first emission.
     */
    fun stream(): Observable<T>

    /**
     * Returns the most recent [value] wrapped in [Single].
     */
    fun single(): Single<T>

    /**
     * Returns the most recent value in plain format.
     */
    fun value(): T
}
