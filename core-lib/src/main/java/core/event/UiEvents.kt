package core.event

import androidx.annotation.MainThread
import androidx.annotation.UiThread
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Base class to aggregate UI events of certain type.
 */
class UiEvents<T : Any> {
    private val publisher = PublishSubject.create<Any>()
    private val internalEvents = PublishSubject.create<T>()

    /**
     * Posts next UI event.
     * @param event UI event.
     */
    @MainThread
    @UiThread
    fun post(event: T) = internalEvents.onNext(event)

    /**
     * Obtains all emitted events as an Observable.
     * @return Observable, that contains all emitted UI events.
     */
    fun stream(): Observable<T> = internalEvents.hide()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    // Listen should return an Observable and not the publisher
    // Using ofType we filter only events that match that class type
    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}