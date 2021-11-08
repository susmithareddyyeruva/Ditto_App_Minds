package com.ditto.data

import com.ditto.home.domain.GetMyLibraryRepository
import com.ditto.home.domain.HomeUsecase
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.mylibrary.domain.model.OfflinePatternData
import com.ditto.home.domain.request.MyLibraryFilterRequestData
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.ProdDomain
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetHomeDataUseCaseImpl @Inject constructor(
    private val homeRepository: GetMyLibraryRepository
) : HomeUsecase {
    override fun getHomePatternsData(requestData: MyLibraryFilterRequestData): Single<Result<MyLibraryDetailsDomain>> {
      return  homeRepository.getHomePatternsData(requestData)
    }

    override fun getOfflinePatternDetails(): Single<Result<List<OfflinePatternData>>> {
        return homeRepository.getOfflinePatternDetails()
    }

    override fun fetchTailornovaTrialPatterns(): Single<Result<List<PatternIdData>>> {
        return homeRepository.fetchTailornovaTrialPatterns()
    }

    override fun getTrialPatterns(): Single<Result<List<ProdDomain>>> {
        return homeRepository.getTrialPatterns()
    }
}