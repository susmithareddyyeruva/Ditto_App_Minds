package com.ditto.home.domain

import com.ditto.home.domain.model.MyLibraryDetailsDomain
import io.reactivex.Single
import non_core.lib.Result

interface MyLibraryUseCase {
    fun getMyLibraryDetails(): Single<Result<MyLibraryDetailsDomain>>
}