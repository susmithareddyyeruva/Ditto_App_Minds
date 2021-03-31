package com.ditto.mylibrary.data

import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.LoginUser
import io.reactivex.Single
import non_core.lib.Result
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.UserDao
import com.ditto.mylibrary.data.mapper.toDomain
import com.ditto.mylibrary.domain.MyLibraryRepository
import com.ditto.mylibrary.domain.model.MyLibraryData
import javax.inject.Inject

/**
 * Concrete class of MyLibraryRepository to expose MyLibrary Data from various sources (API, DB)
 */
class MyLibraryRepositoryImpl @Inject constructor(
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val patternsDao: @JvmSuppressWildcards PatternsDao
) : MyLibraryRepository {
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

    override fun getPatternData(get:Int): Single<Result<MyLibraryData>> {
        return Single.fromCallable {
            val data = patternsDao.getPatternDataByID(get)
            Result.withValue(data.toDomain())
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
