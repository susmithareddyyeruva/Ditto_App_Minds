package com.ditto.menuitems.data

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems.data.api.AboutAppService
import com.ditto.menuitems.data.error.AboutAppFetchError
import com.ditto.menuitems.data.mapper.toDomain
import com.ditto.menuitems.domain.AboutAppAbstractRespository
import com.ditto.menuitems.domain.model.AboutAppDomain
import core.CLIENT_ID
import core.network.NetworkUtility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import javax.inject.Inject

class AboutAppRepositoryImpl @Inject constructor(private val aboutAppService: @JvmSuppressWildcards AboutAppService,
                                                 private val loggerFactory: LoggerFactory
) :AboutAppAbstractRespository{


    @Inject
    lateinit var context: Context
    val logger: Logger by lazy {
        loggerFactory.create(AboutAppRepositoryImpl::class.java.simpleName)
    }

    override fun getAboutAppAndPrivacyData(): Single<Result<AboutAppDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
            return aboutAppService.getAboutAndPrivacyPolicy(CLIENT_ID)
                .doOnSuccess{
                    Log.d("result_success","doOnSuccess >>> ${it.toString()}")

                }
                .map {
                    Result.withValue(it.toDomain())
                }
                .onErrorReturn {
                    var errorMessage = "Error Fetching data"
                    try {
                        logger.d("try block")
                        logger.d("${it.localizedMessage}")
                    } catch (e: Exception) {
                        logger.d("Catch ${e.localizedMessage}")
                        errorMessage = e.message.toString()
                    }
                    Result.withError(
                        AboutAppFetchError(errorMessage, it)
                    )
                }

    }


}