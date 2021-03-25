package core.network

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import java.lang.reflect.Type
import kotlin.reflect.KClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorType(val type: KClass<*>)

class NetworkException(
    val httpCode: Int,
    val error: Any? = null,
    override val message: String = "",
    val throwable: Throwable? = null
) : RuntimeException()

class RxCallAdapterWrapperFactory constructor(
    private val rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
) :
    CallAdapter.Factory() {

    companion object {
        fun createAsync(): RxCallAdapterWrapperFactory {
            return RxCallAdapterWrapperFactory(RxJava2CallAdapterFactory.createAsync())
        }
    }

    private fun handleError(
        annotations: Array<Annotation>,
        retrofit: Retrofit,
        throwable: Throwable
    ): Throwable {

        val errorType: ErrorType? = annotations.find { it is ErrorType } as? ErrorType

        return if (errorType != null && throwable is HttpException) {
            val error = parseError(retrofit, throwable, errorType.type)
            NetworkException(throwable.response()!!.code(), error, throwable.message(), throwable)
        } else throwable
    }

    private fun parseError(
        retrofit: Retrofit,
        httpException: HttpException,
        kClass: KClass<*>
    ): Any? {
        if (httpException.response()!!.isSuccessful) {
            return null
        }
        val errorBody = httpException.response()!!.errorBody() ?: return null
        val converter: Converter<ResponseBody, Any> =
            retrofit.responseBodyConverter(kClass.java, arrayOf())
        return converter.convert(errorBody)
    }

    private inner class RxCallAdapterWrapper constructor(
        private val annotations: Array<Annotation>,
        private val retrofit: Retrofit,
        private val callAdapter: CallAdapter<Any, Any>
    ) : CallAdapter<Any, Any> {

        override fun adapt(call: Call<Any>): Any {
            val any = callAdapter.adapt(call)

            if (any is Observable<*>) {
                return any
                    .doOnNext { throwExceptionForErrorResponse(it) }
                    .onErrorResumeNext(Function {
                        Observable.error(
                            handleError(
                                annotations,
                                retrofit,
                                it
                            )
                        )
                })
            }

            if (any is Maybe<*>) {
                return any
                    .doOnSuccess { throwExceptionForErrorResponse(it) }
                    .onErrorResumeNext(Function {
                        Maybe.error(
                            handleError(
                                annotations,
                                retrofit,
                                it
                            )
                        )
                })
            }

            if (any is Single<*>) {
                return any
                    .doOnSuccess { throwExceptionForErrorResponse(it) }
                    .onErrorResumeNext(Function {
                        Single.error(
                            handleError(
                                annotations,
                                retrofit,
                                it
                            )
                        )
                })
            }

            if (any is Flowable<*>) {
                return any
                    .doOnNext { throwExceptionForErrorResponse(it) }
                    .onErrorResumeNext(Function {
                        Flowable.error(
                            handleError(
                                annotations,
                                retrofit,
                                it
                            )
                        )
                })
            }

            if (any is Completable) {
                return any.onErrorResumeNext(Function {
                    Completable.error(
                        handleError(
                            annotations,
                            retrofit,
                            it
                        )
                    )
                })
            }

            return any
        }

        private fun throwExceptionForErrorResponse(result: Any) {
            if (result is Response<*> && !result.isSuccessful) {
                throw HttpException(result)
            }
        }

        override fun responseType(): Type {
            return callAdapter.responseType()
        }
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        @Suppress("UNCHECKED_CAST")
        val rxJava2CallAdapter: CallAdapter<Any, Any> =
            rxJava2CallAdapterFactory.get(
                returnType,
                annotations,
                retrofit
            ) as CallAdapter<Any, Any>
        return RxCallAdapterWrapper(annotations, retrofit, rxJava2CallAdapter)
    }
}
