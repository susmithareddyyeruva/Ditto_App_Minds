package com.ditto.menuitems.data

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems.data.api.FAQGlossaryService
import com.ditto.menuitems.data.error.FAQGlossaryFetchError
import com.ditto.menuitems.data.mapper.toDomain
import com.ditto.menuitems.domain.FAQGlossaryRepository
import com.ditto.menuitems.domain.model.faq.FAQGlossaryResultDomain
import core.CLIENT_ID
import core.network.NetworkUtility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject


class FAQGlossaryRepositoryImpl @Inject constructor(
    private val faqGlossaryService: FAQGlossaryService,
    private val loggerFactory: LoggerFactory
) : FAQGlossaryRepository {

    @Inject
    lateinit var context: Context
    val logger: Logger by lazy {
        loggerFactory.create(WorkspaceProRepositoryImpl::class.java.simpleName)
    }

    override fun getFAQGlosaaryDetails(): Single<Result<FAQGlossaryResultDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return faqGlossaryService.getFAQGlossaryData(
            CLIENT_ID,
        )
            .doOnSuccess {
                logger.d("*****FETCH FAQ and GLOSSARY SUCCESS**")
            }
            .map {
                Result.withValue(it.toDomain())


            }
            .onErrorReturn {
                var errorMessage = "Error Fetching data"
                try {
                    logger.d("try block")
                } catch (e: Exception) {
                    Log.d("Catch", e.localizedMessage)
                    errorMessage = when (e) {
                        is UnknownHostException -> {
                            "Unknown host!"
                        }
                        is ConnectException -> {
                            "No Internet connection available !"
                        }
                        else -> {
                            "Error Fetching data!"
                        }
                    }
                }


                Result.withError(
                    FAQGlossaryFetchError(errorMessage, it)
                )
            }
    }


}
