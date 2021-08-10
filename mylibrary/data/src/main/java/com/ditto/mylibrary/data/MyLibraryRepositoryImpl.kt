package com.ditto.mylibrary.data

import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.api.LoginRepositoryImpl
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.data.api.TailornovaApiService
import com.ditto.mylibrary.data.mapper.toDomain
import com.ditto.mylibrary.domain.MyLibraryRepository
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.storage.data.database.OfflinePatternDataDao
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.UserDao
import core.lib.BuildConfig
import core.models.CommonApiFetchError
import io.reactivex.Single
import non_core.lib.Result
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Concrete class of MyLibraryRepository to expose MyLibrary Data from various sources (API, DB)
 */
class MyLibraryRepositoryImpl @Inject constructor(
    private val tailornovaApiService: @JvmSuppressWildcards TailornovaApiService,
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val patternsDao: @JvmSuppressWildcards PatternsDao,
    private val offlinePatternDataDao: @JvmSuppressWildcards OfflinePatternDataDao,
    private val loggerFactory: LoggerFactory
) : MyLibraryRepository {

    val logger: Logger by lazy {
        loggerFactory.create(LoginRepositoryImpl::class.java.simpleName)
    }

    /**
     * fetches data from local store first. if not available locally, fetches from server
     */
    override fun getMyLibraryData(): Single<Result<List<MyLibraryData>>> {
        return Single.fromCallable {
            //fetch from local DB
            val data = patternsDao.getPatternsData()
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

    override fun getPatternData(get:String): Single<Result<PatternIdData>> {
        return tailornovaApiService.getPatternDetailsByDesignId(BuildConfig.TAILORNOVA_BASEURL+get)
            .doOnSuccess {
                logger.d("*****Tailornova Success**")
                // patternType!= trial >> delete it
                offlinePatternDataDao.deleteDemoPattern("trial")
                offlinePatternDataDao.insertOfflinePatternData(it.toDomain())
                //insert to db
            }.map {
                Result.withValue(it)
            }
            .onErrorReturn {
                var errorMessage = "Error Fetching data"
                try {
                    logger.d("try block")
                    val error = it as HttpException
                    if (error != null) {
                        logger.d("Error Tailornova")
                    }
                } catch (e: Exception) {
                    Log.d("Catch", e.localizedMessage)
                    errorMessage = e.message.toString()
                }
                Result.withError(
                    CommonApiFetchError(errorMessage, it)
                )
            }
    }

    override fun completeProject(patternId: Int): Single<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removePattern(patternId: Int): Single<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


//    override fun addProject(
//        id: Int,
//        dndOnboarding: Boolean,
//        isLaterClicked: Boolean
//    ): Single<Any> {
//        return Single.fromCallable {
//            dbDataDao.updateDndOnboarding(id,dndOnboarding, isLaterClicked)
//        }
//    }
}
