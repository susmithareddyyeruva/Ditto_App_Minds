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
                CalibrationErrorCode.PatternImageIsCropped -> {
                    //imageArray.clear()
                    // show alert?
                    Log.d("Calibration", "PatternImageIsCropped")
                    callback.onCalibrationReponse(CalibrationType.PatternImageIsCropped)
                }
                CalibrationErrorCode.CameraDistanceTooFarBack -> {
                    // show alert?
                    Log.d("Calibration", "CameraDistanceTooFarBack")
                    callback.onCalibrationReponse(CalibrationType.CameraDistanceTooFarBack)
                }
                CalibrationErrorCode.CameraHeightTooLow -> {
                    Log.d("Calibration", "CameraHeightTooLow")
                    callback.onCalibrationReponse(CalibrationType.CameraHeightTooLow)
                    // what to do?
                }
                CalibrationErrorCode.CameraTooFarLeftOrRight -> {
                    Log.d("Calibration", "CameraTooFarLeftOrRight")
                    callback.onCalibrationReponse(CalibrationType.CameraTooFarLeftOrRight)
                    // what to do?
                }
                CalibrationErrorCode.OrientationNotLandscape -> {
                    Log.d("Calibration", "OrientationNotLandscape")
                    callback.onCalibrationReponse(CalibrationType.OrientationNotLandscape)
                    // what to do?
                }

                CalibrationErrorCode.CameraResolutionTooLow -> {
                    Log.d("Calibration", "CameraResolutionTooLow")
                    callback.onCalibrationReponse(CalibrationType.CameraResolutionTooLow)
                }
                //----------------
                CalibrationErrorCode.MatIsRotated180Degrees,
                -> {
                    Log.d("Calibration", "FailCalibration")
                    callback.onCalibrationReponse(CalibrationType.FailCalibration)
                }

                CalibrationErrorCode.ImageTooBlurr -> {
                    Log.d("Calibration", "FailCalibration")
                    callback.onCalibrationReponse(CalibrationType.FailCalibration)
                }

                CalibrationErrorCode.ImageTooBright -> {
                    Log.d("Calibration", "FailCalibration")
                    callback.onCalibrationReponse(CalibrationType.FailCalibration)
                }

                CalibrationErrorCode.FailCalibration -> {
                    Log.d("Calibration", "FailCalibration")
                    callback.onCalibrationReponse(CalibrationType.FailCalibration)
                }

                else -> {
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
        PatternImageIsCropped,
        CameraDistanceTooFarBack,
        CameraHeightTooLow,
        CameraTooFarLeftOrRight,
        OrientationNotLandscape,
        CameraResolutionTooLow,
        MatIsRotated180Degrees,
        ImageTooBlurr,
        ImageTooBright,
        FailCalibration,
        Else
    }
}