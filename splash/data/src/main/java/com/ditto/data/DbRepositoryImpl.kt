package com.ditto.data

import android.content.Context
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.domain.DbRepository
import com.ditto.login.domain.LoginUser
import com.joann.fabrictracetransform.transform.performTransform
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import com.ditto.storage.data.database.OnBoardingDao
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.TraceDataDatabase
import com.ditto.storage.data.database.UserDao
import core.ui.common.Utility
import javax.inject.Inject

/**
 * Concrete class of Repository to expose Data from various sources (API, DB)
 */
class DbRepositoryImpl @Inject constructor(
    private val dbUserDataDao: @JvmSuppressWildcards UserDao,
    private val dbDataDao: @JvmSuppressWildcards OnBoardingDao,
    private val dbPatternsDao: @JvmSuppressWildcards PatternsDao
) : DbRepository {

    @Inject
    lateinit var context: Context

    /**
     * fetches data from local store first. if not available locally, inserts from local json
     */
    override fun getDbData(): Single<Any> {
        return Single.fromCallable {
            val data = dbDataDao.getOnboardingData()
            if (data.isEmpty()) {
                TraceDataDatabase.preLoadOnboardingData(context)
            }
        }.flatMap {
            updatePatternsData()
        }.flatMap {
            transform()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    private fun updatePatternsData(): Single<Any> {
        return Single.fromCallable {
            val data = dbPatternsDao.getPatternsData()
            if (data.isEmpty()) {
                TraceDataDatabase.preLoadPatternData(context)
            }
        }
    }

    /*
    Dummy call to Transform Library for optimization
     */
    private fun transform(): Single<Any> {
        val bitmap = Utility.getBitmapFromDrawable("calibration_pattern", context)
        return Single.fromCallable {
            performTransform(bitmap, context.applicationContext, null)
        }
    }

    /**
     * fetches User data from local store.
     */
    override fun getUser(): Single<Result<LoginUser>> {
        return Single.fromCallable {
            val data = dbUserDataDao.getUserData()
            if (data != null)
                Result.withValue(data.toUserDomain())
            else
                Result.withValue(LoginUser(""))
        }
    }
}