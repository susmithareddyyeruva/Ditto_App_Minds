package trace.mylibrary.domain

import com.ditto.login.domain.LoginUser
import non_core.lib.Result
import io.reactivex.Single
import trace.mylibrary.domain.model.MyLibraryData

interface GetMylibraryData {
    fun invoke() : Single<Result<List<MyLibraryData>>>
    fun getUser(): Single<Result<LoginUser>>
    fun getPattern(get: Int): Single<Result<MyLibraryData>>
    //fun addProject( id : Int): Single<Any>
    fun removeProject(patternId: Int): Single<Any>
    fun completeProject(patternId: Int): Single<Any>
}

