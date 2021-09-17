package com.ditto.data

import com.ditto.home.domain.GetMyLibraryRepository
import com.ditto.home.domain.HomePageUseCase
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.model.OfflinePatternData
import com.ditto.home.domain.request.MyLibraryFilterRequestData
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetHomeDataUseCaseImpl @Inject constructor(
    private val homeRepository: GetMyLibraryRepository
) : HomePageUseCase {
    override fun getMyLibraryDetails(requestData: MyLibraryFilterRequestData): Single<Result<MyLibraryDetailsDomain>> {
      return  homeRepository.getMyLibraryDetails(requestData)
    }

    override fun getOfflinePatternDetails(): Single<Result<List<OfflinePatternData>>> {
        return homeRepository.getOfflinePatternDetails()
    }
}