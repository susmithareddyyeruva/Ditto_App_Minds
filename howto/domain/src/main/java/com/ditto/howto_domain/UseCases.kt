package com.ditto.howto_domain

import com.ditto.howto_domain.model.HowToData
import non_core.lib.Result
import io.reactivex.Single

/**
 * Created by Sesha on  15/08/2020.
 * Usecase class invoking from view model
 */
interface GetHowToDataUsecase{
    fun invoke(id: Int) : Single<Result<HowToData>>
}