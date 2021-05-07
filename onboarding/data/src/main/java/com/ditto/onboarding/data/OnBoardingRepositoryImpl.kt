package com.ditto.onboarding.data

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.api.LoginRepositoryImpl
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.LoginUser
import com.ditto.onboarding.data.api.OnBoardingService
import com.ditto.onboarding.data.error.ContentApiFetchError
import com.ditto.onboarding.data.mapper.toDomain
import com.ditto.onboarding.data.mapper.toStorage
import com.ditto.onboarding.domain.OnboardingRepository
import com.ditto.onboarding.domain.model.OnBoardingResultDomain
import com.ditto.onboarding.domain.model.OnboardingData
import com.ditto.storage.data.database.OnBoardingDao
import com.ditto.storage.data.database.UserDao
import core.CLIENT_ID
import core.network.Utility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Concrete class of OnBoardingRepository to expose OnBoarding Data from various sources (API, DB)
 */
class OnBoardingRepositoryImpl @Inject constructor(
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val onboardingDao: @JvmSuppressWildcards OnBoardingDao,
    private val onBoardingService: @JvmSuppressWildcards OnBoardingService,
    private val loggerFactory: LoggerFactory
) : OnboardingRepository {
    @Inject
    lateinit var context: Context
    val logger: Logger by lazy {
        loggerFactory.create(LoginRepositoryImpl::class.java.simpleName)
    }

    /**
     * fetches data from local store first. if not available locally, fetches from server
     */
    override fun getOnboardingData(): Single<Result<List<OnboardingData>>> {
        return Single.fromCallable {
            //fetch from local DB
            val data = onboardingDao.getOnboardingData()
            Result.withValue(data.toDomain())
        }
    }

    override fun getOnboardingContent(): Single<Result<OnBoardingResultDomain>> {
        if (!Utility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return onBoardingService.getContentApi(
            CLIENT_ID,
        )
            .doOnSuccess {
                logger.d("*****Onboarding Success**")
                val data= it.cBody.onboarding.toStorage()
                onboardingDao.insertAllOnboardingData(data)

            /*    try {
                    val data= it.cBody.onboarding.toStorage()
                    onboardingDao.insertAllOnboardingData(data)
                    val collectionType = object : TypeToken<Collection<OnBoarding>>() {}.type
                    val lcs = Gson().fromJson(it.toString(), collectionType) as List<OnBoarding>
                    Executors.newSingleThreadExecutor()
                        .execute(Runnable { onboardingDao.insertAllOnboardingData(lcs) })
                    logger.d("insertOnboardingData complete")
                } catch (e: JSONException) {
                    logger.d("insertOnboardingData  exception")
                }*/

                /*  val data= it.cBody.onboarding.toStorage()
                  onboardingDao.insertAllOnboardingData(data)*/
            }
            .map {
                Result.withValue(it.toDomain())


            }
            .onErrorReturn {
                var errorMessage = "Error Fetching data"
                try {
                    logger.d("try block")
                    val error = it as HttpException
                    if (error != null) {
                        logger.d("Error Onboarding")
                    }
                } catch (e: Exception) {
                    Log.d("Catch", e.localizedMessage)
                    errorMessage = e.message.toString()


                }


                Result.withError(
                    ContentApiFetchError(errorMessage, it)
                )
            }
    }

    /**
     * creates user data to local store.
     */
    override fun getUserData(): Single<Result<LoginUser>> {
        return Single.fromCallable {
            val data = dbDataDao.getUserData()
            if (data != null)
                Result.withValue(data.toUserDomain())
            else
                Result.withValue(LoginUser(""))
        }
    }


    override fun updateDontShowThisScreen(
        id: Int,
        dndOnboarding: Boolean,
        isBleLaterClicked: Boolean,
        isWifiLaterClicked: Boolean
    ): Single<Any> {
        return Single.fromCallable {
            dbDataDao.updateDndOnboardingUser(
                id,
                dndOnboarding,
                isBleLaterClicked,
                isWifiLaterClicked
            )
        }
    }
}
