package com.ditto.calibration.ui

import android.util.Log
import com.joann.fabrictracetransform.calibrate.CalibrationErrorCode

class Util {
    companion object {
        internal fun handleResult(calibrationResponse: CalibrationErrorCode, callback: Callback) {
            Log.d("Calibration", "Calibration handleResult - $calibrationResponse")
            when (calibrationResponse) {
                CalibrationErrorCode.Success -> {
                    Log.d("Calibration", "Success")
                    callback.onCalibrationReponse(CalibrationType.Success)
                }
                CalibrationErrorCode.InvalidImageFormat -> {
                    //imageArray.clear()
                    // show alert?
                    Log.d("Calibration", "InvalidImageFormat")
                    callback.onCalibrationReponse(CalibrationType.InvalidImageFormat)
                }
                CalibrationErrorCode.FailCalibration -> {
                    // show alert?
                    Log.d("Calibration", "FailCalibration")
                    callback.onCalibrationReponse(CalibrationType.FailCalibration)
                }
                CalibrationErrorCode.TooManyImages -> {
                    Log.d("Calibration", "TooManyImages")
                    callback.onCalibrationReponse(CalibrationType.TooManyImages)
                    // what to do?
                }
                CalibrationErrorCode.FailExtractingFeaturePoints -> {
                    Log.d("Calibration", "FailExtractingFeaturePoints")
                    callback.onCalibrationReponse(CalibrationType.FailExtractingFeaturePoints)
                    // what to do?
                }
                CalibrationErrorCode.FailSavingCalibrationResult -> {
                    Log.d("Calibration", "FailSavingCalibrationResult")
                    callback.onCalibrationReponse(CalibrationType.FailSavingCalibrationResult)
                    // what to do?
                }

                CalibrationErrorCode.CameraPixelsTooLow -> {
                    Log.d("Calibration", "CameraPixelsTooLow")
                    callback.onCalibrationReponse(CalibrationType.CameraPixelsTooLow)
                }

                CalibrationErrorCode.IncorrectImageOrientation -> {
                    Log.d("Calibration", "IncorrectImageOrientation")
                    callback.onCalibrationReponse(CalibrationType.IncorrectImageOrientation)
                }

                CalibrationErrorCode.PatternImageIsCropped -> {
                    Log.d("Calibration", "PatternImageIsCropped")
                    callback.onCalibrationReponse(CalibrationType.PatternImageIsCropped)
                }

                CalibrationErrorCode.ImageTakenFromProjectorSide -> {
                    Log.d("Calibration", "ImageTakenFromProjectorSide")
                    callback.onCalibrationReponse(CalibrationType.ImageTakenFromProjectorSide)
                }

                else->{
                    Log.d("Calibration", "CalibrationErrorCode Else - $calibrationResponse")
                    callback.onCalibrationReponse(CalibrationType.Else)
                }
            }
        }
    }

    internal interface Callback {
        fun onCalibrationReponse(calibrationResponse: CalibrationType)
    }

    enum class CalibrationType {
        Success,

        InvalidImageFormat,

        FailExtractingFeaturePoints,

        FailCalibration,

        TooManyImages,

        FailSavingCalibrationResult,

        CameraPixelsTooLow,

        IncorrectImageOrientation,

        UnableToDetectObjects,

        PatternImageIsCropped,

        ProjectedRegionNotProper,

        ImageTakenFromProjectorSide,

        MatIsRotated180Degrees,

        Else
    }
}