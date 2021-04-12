package com.ditto.calibration.ui

import android.util.Log
import com.joann.fabrictracetransform.calibrate.CalibrationErrorCode

class Util {
    companion object {
        internal fun handleResult(calibrationResponse: CalibrationErrorCode, callback: Callback) {
            //logger.d("calibration - $calibrationResponse")
            when (calibrationResponse) {
                CalibrationErrorCode.Success -> {
                    Log.d("Calibration", "Success")
                    callback.OnCalibrationReponse(CalibrationType.Success)
                }
                CalibrationErrorCode.InvalidImageFormat -> {
                    //imageArray.clear()
                    // show alert?
                    Log.d("Calibration", "InvalidImageFormat")
                    callback.OnCalibrationReponse(CalibrationType.InvalidImageFormat)
                }
                CalibrationErrorCode.FailCalibration -> {
                    // show alert?
                    Log.d("Calibration", "FailCalibration")
                    callback.OnCalibrationReponse(CalibrationType.FailCalibration)
                }
                CalibrationErrorCode.TooManyImages -> {
                    Log.d("Calibration", "TooManyImages")
                    callback.OnCalibrationReponse(CalibrationType.TooManyImages)
                    // what to do?
                }
                CalibrationErrorCode.FailExtractingFeaturePoints -> {
                    Log.d("Calibration", "FailExtractingFeaturePoints")
                    callback.OnCalibrationReponse(CalibrationType.FailExtractingFeaturePoints)
                    // what to do?
                }
                CalibrationErrorCode.FailSavingCalibrationResult -> {
                    Log.d("Calibration", "FailSavingCalibrationResult")
                    callback.OnCalibrationReponse(CalibrationType.FailSavingCalibrationResult)
                    // what to do?
                }
            }
        }
    }

    internal interface Callback {
        fun OnCalibrationReponse(calibrationResponse: CalibrationType)
    }

    enum class CalibrationType {
        Success,

        InvalidImageFormat,

        FailExtractingFeaturePoints,

        FailCalibration,

        TooManyImages,

        FailSavingCalibrationResult
    }
}