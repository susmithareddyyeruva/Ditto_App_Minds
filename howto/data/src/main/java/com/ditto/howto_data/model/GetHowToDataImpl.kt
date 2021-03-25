package com.ditto.howto_data.model


import com.ditto.howto_domain.GetHowToDataRepository
import com.ditto.howto_domain.GetHowToDataUsecase
import com.ditto.howto_domain.model.HowToData

import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

/**
 * Created by Sesha on  15/08/2020.
 * Class representing functions from usecase class
 */
class GetHowToDataImpl @Inject constructor(
    private val howtoRepository: GetHowToDataRepository
) : GetHowToDataUsecase {
    override fun invoke(id: Int): Single<Result<HowToData>> {
        return howtoRepository.gethowtodata(id)
    }
}