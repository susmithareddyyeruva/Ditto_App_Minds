package com.ditto.onboarding.data

import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.LoginUser
import io.reactivex.Single
import non_core.lib.Result
import com.ditto.storage.data.database.OnBoardingDao
import com.ditto.storage.data.database.UserDao
import com.ditto.onboarding.data.mapper.toDomain
import com.ditto.onboarding.domain.OnboardingRepository
import com.ditto.onboarding.domain.model.OnboardingData
import javax.inject.Inject

/**
 * Concrete class of OnBoardingRepository to expose OnBoarding Data from various sources (API, DB)
 */
class OnBoardingRepositoryImpl @Inject constructor(
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val onboardingDao: @JvmSuppressWildcards OnBoardingDao
) : OnboardingRepository {
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
            dbDataDao.updateDndOnboardingUser(id,dndOnboarding, isBleLaterClicked, isWifiLaterClicked)
        }
    }
}
