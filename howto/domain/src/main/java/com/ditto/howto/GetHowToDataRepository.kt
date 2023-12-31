package com.ditto.howto


import com.ditto.howto.model.HowToData
import io.reactivex.Single
import non_core.lib.Result

/**
 * Created by Sesha on  15/08/2020.
 * Repository class with functions
 */

interface GetHowToDataRepository {
    fun gethowtodata(id: Int): Single<Result<HowToData>>
}