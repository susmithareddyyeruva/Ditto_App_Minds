package trace.mylibrary.domain

import com.ditto.login.domain.LoginUser
import io.reactivex.Single
import non_core.lib.Result
import trace.mylibrary.domain.model.MyLibraryData

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface MyLibraryRepository {
    fun getMyLibraryData(): Single<Result<List<MyLibraryData>>>
    fun getUserData(): Single<Result<LoginUser>>
    fun getPatternData(get:Int): Single<Result<MyLibraryData>>
    fun completeProject(patternId:Int): Single<Any>
    fun removePattern(patternId: Int): Single<Any>
    //fun addProject(id : Int): Single<Any>
}