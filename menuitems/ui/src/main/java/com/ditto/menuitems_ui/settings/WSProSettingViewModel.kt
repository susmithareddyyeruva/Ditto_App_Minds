package com.ditto.menuitems_ui.settings

import android.util.Log
import com.ditto.login.domain.LoginUser
import com.ditto.menuitems.domain.GetWorkspaceProData
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import javax.inject.Inject


class WSProSettingViewModel @Inject constructor(
    private val getWorkspaceProData: GetWorkspaceProData

) : BaseViewModel() {

    fun fetchUserData() {
        disposable += getWorkspaceProData.getUserDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleFetchResult(result: Result<LoginUser>?) {
        when (result) {
            is Result.OnSuccess<LoginUser> -> {
                Log.d("WSProSettingViewModel", result.toString())
            }

            is Result.OnError -> {
                Log.d("WSProSettingViewModel", "Failed")
            }
        }
    }

    // need to call on switch change
    private fun updateWSProSetting(){
        disposable += getWorkspaceProData.updateWSProSetting(
            id = 1, cMirrorReminder = true, cCuttingReminder = true,
            cSpliceMultiplePieceReminder = true, cSpliceReminder = true
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { it }
    }


}