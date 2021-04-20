package com.ditto.howto

import com.ditto.howto.model.HowToData
import io.reactivex.Single
import non_core.lib.Result

/**
 * Created by Sesha on  15/08/2020.
 * Usecase class invoking from view model
 */
interface GetHowToDataUsecase{
    fun invoke(id: Int) : Single<Result<HowToData>>
}