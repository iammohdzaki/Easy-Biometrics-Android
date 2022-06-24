package com.cosmos.biometrics

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

private const val TAG = "BiometricsHelper"

/**
Created by Mohammad Zaki
on Oct,25 2021
 **/
class BiometricsDialogHelper(var builder: Builder) {
    private val context: Context = builder.context

    class Builder(var context: Context) {

        var title: String = ""
        var description: String = ""
        var negativeText: String = ""
        var callback: Callback? = null

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun negativeText(negativeText: String): Builder {
            this.negativeText = negativeText
            return this
        }

        fun callback(callback: Callback): Builder {
            this.callback = callback
            return this
        }

        fun create(): BiometricsDialogHelper {
            return BiometricsDialogHelper(this)
        }
    }


    fun show() {
        BiometricUtils.canAuthenticate(context, object : Callback {
            override fun success() {
                checkBiometric()
            }

            override fun failure(message: String) {
                builder.callback?.failure(message)
            }
        })
    }

    private fun checkBiometric() {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(context as FragmentActivity, executor, object :
            BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                builder.callback?.success()
                Log.d(TAG, "onAuthenticationSucceeded: Success")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                builder.callback?.failure(errString.toString())
                Log.d(TAG, "onAuthenticationError: $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                builder.callback?.failure("Something went wrong!")
                Log.d(TAG, "onAuthenticationFailed: Something went wrong!")
            }
        })


        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(builder.title)
            .setDescription(builder.description)
            .setNegativeButtonText(builder.negativeText)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

}

interface Callback {
    fun success()
    fun failure(message: String)
}