package com.ditto.mylibrary.ui.util

interface CallbackDialogListener {
        fun onSaveButtonClicked(projectName: String, isCompleted: Boolean?)
        fun onExitButtonClicked()
    }
