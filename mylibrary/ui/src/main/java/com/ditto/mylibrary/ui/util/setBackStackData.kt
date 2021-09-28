package com.ditto.mylibrary.ui.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController

fun <T> Fragment.setBackStackData(key: String, data: T, doBack: Boolean = false) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, data)
        if (doBack)
            findNavController().popBackStack()
    }
    
    fun <T> Fragment.getBackStackData(key: String, singleCall : Boolean= true, result: (T) -> (Unit)) {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
            ?.observe(viewLifecycleOwner) {
                result(it)
                //if not removed then when click back without set data it will return previous data
                if(singleCall) findNavController().currentBackStackEntry?.savedStateHandle?.remove<T>(key)
            }
    }