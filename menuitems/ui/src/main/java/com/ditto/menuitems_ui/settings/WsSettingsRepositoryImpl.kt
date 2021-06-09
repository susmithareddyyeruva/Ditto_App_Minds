package com.ditto.menuitems_ui.settings

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems_ui.settings.data.LoginResult
import com.ditto.menuitems_ui.settings.data.WsSettingsPostError
import com.ditto.menuitems_ui.settings.domain.SettingsRepository
import com.ditto.menuitems_ui.settings.model.WSSettingsInputData
import core.CLIENT_ID
import core.network.Utility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import retrofit2.HttpException
import javax.inject.Inject

class WsSettingsRepositoryImpl @Inject constructor(
    private val ws_settings: @JvmSuppressWildcards WsSettingsService,
    private val loggerFactory: LoggerFactory
) : SettingsRepository {

    @Inject
    lateinit var context: Context
    val logger: Logger by lazy {
        loggerFactory.create(WsSettingsRepositoryImpl::class.java.simpleName)
    }

//    override fun postSwitchData(data: WSSettingsInputData): Single<Result<LoginResult>> {
//        if (!Utility.isNetworkAvailable(context)) {
//            return Single.just(Result.OnError(NoNetworkError()))
//        }
//        return ws_settings.postSettingRequest(
//            CLIENT_ID,
//            data
//        ).doOnSuccess(
//            Log.d("Post", "*****Setting Post Success**")
//        ) .onErrorReturn {
//            var errorMessage = "Error Fetching data"
//            try {
//                logger.d("try block")
//                val error = it as HttpException
//                if (error != null) {
//                    logger.d("Error Onboarding")
//                }
//            } catch (e: Exception) {
//                Log.d("Catch", e.localizedMessage)
//                errorMessage = e.message.toString()
//            }
//            Result.withError(
//                WsSettingsPostError(errorMessage, it)
//            )
//        }
//    }
}