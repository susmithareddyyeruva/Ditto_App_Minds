package com.ditto.home.domain

import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.request.MyLibraryFilterRequestData
import io.reactivex.Single
import non_core.lib.Result

interface MyLibraryUseCase {
    fun getMyLibraryDetails(requestData: MyLibraryFilterRequestData): Single<Result<MyLibraryDetailsDomain>>
}