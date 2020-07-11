package com.marty.dang.biometricprompt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import timber.log.Timber

// The new Biometrics API replaces the outdated fingerprintCompat API. This new library is very easy to implement but
// at the moment, it lacks the control you would have if you just used fingerprintCompat.
// 1. Not able to customize the biometric prompt interface
// 2. If a device has both fingerprint scanner and face scanner, which one is used? Can we control that?
// before P, it would use fingerprints though after android chooses the best one. So we don't have fine grain control like fingerprintCompat

class MainActivity : AppCompatActivity() {

    lateinit var biometricButton: Button
    lateinit var prompt: BiometricPrompt
    var canAuthenticate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check if biometric auth is possible.
        val biometricManager = BiometricManager.from(this)
        when(biometricManager.canAuthenticate()){
            BiometricManager.BIOMETRIC_SUCCESS -> {
                prompt = createBiometricPromptInstance()
                canAuthenticate = true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Timber.d("No biometric features avil")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Timber.d("currently unavalible")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Timber.d("No biometrics set up")
        }

        // quick and dirty button setup
        biometricButton = findViewById(R.id.biometric_button)
        biometricButton.setOnClickListener {
            if(canAuthenticate){
                prompt.authenticate(createPrompt())
            } else {
                Toast.makeText(applicationContext,"Can't do anything", Toast.LENGTH_LONG).show();
            }
        }
    }

    // callback from biometric results come in here.
    private fun createBiometricPromptInstance(): BiometricPrompt {

        val executer =  ContextCompat.getMainExecutor(this)
        val callback = object: BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext,"Success", Toast.LENGTH_LONG).show();
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext,"Failed", Toast.LENGTH_LONG).show();
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext,"Error $errString", Toast.LENGTH_LONG).show();
            }
        }
        return BiometricPrompt(this, executer, callback)
    }

    // setDeviceCredentailAllowed - Lets a user whose device doesn't support fingerprint auth to use password or pattern instead
    // Can't have both negative button behavior and device credential enabled

    private fun createPrompt(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Prompt")
            .setSubtitle("Please authenticate")
            .setDescription("I need you to validate yourself")
//            .setNegativeButtonText("Cancel")
            .setConfirmationRequired(true)
            .setDeviceCredentialAllowed(true)
            .build()
    }
}