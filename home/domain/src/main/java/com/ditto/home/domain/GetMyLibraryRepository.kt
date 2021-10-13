package com.ditto.home.domain

import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.request.MyLibraryFilterRequestData
import com.ditto.mylibrary.domain.model.OfflinePatternData
import com.ditto.mylibrary.domain.model.PatternIdData
import io.reactivex.Single
import non_core.lib.Result

interface GetMyLibraryRepository {
    fun getHomePatternsData(requestData: MyLibraryFilterRequestData): Single<Result<MyLibraryDetailsDomain>>
    fun getOfflinePatternDetails(): Single<Result<List<OfflinePatternData>>>
    fun fetchTailornovaTrialPatterns(): Single<Result<List<PatternIdData>>>
}