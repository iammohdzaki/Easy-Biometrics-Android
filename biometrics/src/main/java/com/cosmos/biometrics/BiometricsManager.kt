@file:Suppress("DEPRECATION")

package com.cosmos.biometrics

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.FINGERPRINT_SERVICE
import android.content.Context.KEYGUARD_SERVICE
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

/**
Created by Mohammad Zaki
on Jun,24 2022
 **/
class BiometricsManager(
    var context: Context,
    var secretKey: String = "BiometricsManager",
    var authEvent: AuthEvent
) : FingerprintManager.AuthenticationCallback() {

    private val keyguardManager: KeyguardManager =
        context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
    private val fingerprintManager: FingerprintManager =
        context.getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
    private lateinit var cancellationSignal: CancellationSignal
    private lateinit var keyStore: KeyStore
    private lateinit var cipher: Cipher

    private val ANDROID_KEY_STORE = "AndroidKeyStore"

    /**
     * Register Auth Event By Calling this method
     */
    fun registerAuthEvent() {
        if (canAuthenticate()) {
            initKeyStore()
            if (initializeCipher()) {
                val cryptoObject = FingerprintManager.CryptoObject(cipher)
                cancellationSignal = CancellationSignal()
                fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
            }
        }
    }

    /**
     * Remove Auth Event To Stop Listening to auth event
     */
    fun removeAuthEvent() {
        if (this::cancellationSignal.isInitialized){
            cancellationSignal.cancel()
        }
    }

    private fun initKeyStore() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null);

            val keyGenerator: KeyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    secretKey,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7
                    )
                    .build()
            )
            keyGenerator.generateKey()
        } catch (e: Exception) {
            authEvent.onInitError()
            e.printStackTrace()
        }
    }

    private fun initializeCipher(): Boolean {
        try {
            val cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
            val key: SecretKey
            val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null)
            key = keyStore.getKey(secretKey, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return true
        } catch (e: KeyPermanentlyInvalidatedException) {
            authEvent.onInitError()
            return false
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }

    private fun canAuthenticate(): Boolean {
        //Check if the device does not contain the fingerprint hardware.
        if (!fingerprintManager.isHardwareDetected) {
            return false
        } else {
            // Checks whether fingerprint permission is set on manifest
            return if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.USE_FINGERPRINT
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                false
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    false
                } else {
                    // Checks whether lock screen security is enabled or not
                    keyguardManager.isKeyguardSecure
                }
            }
        }
    }

    /**
     * Called when an unrecoverable error has been encountered and the operation is complete.
     * No further callbacks will be made on this object.
     * @param errorCode An integer identifying the error message
     * @param errString A human-readable error string that can be shown in UI
     */
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        super.onAuthenticationError(errorCode, errString)
        authEvent.onAuthenticationError(errString.toString())
    }

    /**
     * Called when a recoverable error has been encountered during authentication. The help
     * string is provided to give the user guidance for what went wrong, such as
     * "Sensor dirty, please clean it."
     * @param helpCode An integer identifying the error message
     * @param helpString A human-readable string that can be shown in UI
     */
    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        super.onAuthenticationHelp(helpCode, helpString)
        authEvent.onAuthenticationHelp(helpString.toString())
    }

    /**
     * Called when a fingerprint is recognized.
     * @param result An object containing authentication-related data
     */
    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        super.onAuthenticationSucceeded(result)
        authEvent.onAuthenticationSucceeded()
    }

}

interface AuthEvent {
    fun onInitError()
    fun onAuthenticationSucceeded()
    fun onAuthenticationHelp(message: String)
    fun onAuthenticationError(message: String)
}