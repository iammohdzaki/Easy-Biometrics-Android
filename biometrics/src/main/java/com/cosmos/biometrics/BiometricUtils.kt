package com.cosmos.biometrics

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager

private const val TAG = "BiometricUtils"

/**
Created by Mohammad Zaki
on Oct,25 2021
 **/
object BiometricUtils {

    fun canAuthenticate(context: Context, callback: Callback) {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                callback.failure("Device doesn't have a fingerprint!")
                Log.d(TAG, "init: Device doesn't have a fingerprint!")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                callback.failure("Not Working!")
                Log.d(TAG, "init: Not Working!")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                callback.failure("No Fingerprint is assigned!")
                Log.d(TAG, "init: No Fingerprint is assigned!")
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                callback.failure("Security Update Needed")
                Log.d(TAG, "init: Security Update Needed!")
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                callback.failure("Unsupported")
                Log.d(TAG, "init: Unsupported!")
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                callback.failure("Status unknown")
                Log.d(TAG, "init: Status unknown!")
            }
            BiometricManager.BIOMETRIC_SUCCESS -> {
                callback.success()
                Log.d(TAG, "init: Success!")
            }
        }
    }

}