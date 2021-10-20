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

                CalibrationErrorCode.CameraPixelsTooLow -> {
                    Log.d("Calibration", "CameraPixelsTooLow")
                    callback.OnCalibrationReponse(CalibrationType.CameraPixelsTooLow)
                }

                CalibrationErrorCode.IncorrectImageOrientation -> {
                    Log.d("Calibration", "IncorrectImageOrientation")
                    callback.OnCalibrationReponse(CalibrationType.IncorrectImageOrientation)
                }

                CalibrationErrorCode.PatternImageIsCropped -> {
                    Log.d("Calibration", "PatternImageIsCropped")
                    callback.OnCalibrationReponse(CalibrationType.PatternImageIsCropped)
                }

                CalibrationErrorCode.ImageTakenFromProjectorSide -> {
                    Log.d("Calibration", "ImageTakenFromProjectorSide")
                    callback.OnCalibrationReponse(CalibrationType.ImageTakenFromProjectorSide)
                }

                else->{
                    Log.d("Calibration", "CalibrationErrorCode Else - $calibrationResponse")
                    callback.OnCalibrationReponse(CalibrationType.Else)
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