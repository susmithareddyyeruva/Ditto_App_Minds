package com.ditto.data

import com.ditto.home.domain.GetMyLibraryRepository
import com.ditto.home.domain.MyLibraryUseCase
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetHomeDataUseCaseImpl @Inject constructor(
    private val homeRepository: GetMyLibraryRepository
) : MyLibraryUseCase {
    override fun getMyLibraryDetails(): Single<Result<MyLibraryDetailsDomain>> {
      return  homeRepository.getMyLibraryDetails()
    }

}