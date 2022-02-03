package com.ditto.menuitems.data.api

import com.ditto.menuitems.data.model.DeleteAccountInfoResult
import com.ditto.menuitems.domain.model.AboutAppResponseData
import core.lib.BuildConfig
import io.reactivex.Single
import retrofit2.http.*

interface AccountInfoService {
    @Headers("Content-Type: application/json")
    @DELETE(core.lib.BuildConfig.WORKSPACE_ENDURL + "customer_lists/ditto/customers/{cust_no}")
    fun deleteAccountInfo(
        @Path("cust_no") cust_no : String,@Header("Authorization") header:String
    ):Single<DeleteAccountInfoResult>
}