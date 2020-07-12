package com.marty.dang.biometricprompt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import timber.log.Timber

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MainFrag : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var biometricButton: Button
    lateinit var prompt: BiometricPrompt
    var canAuthenticate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // check if biometric auth is possible.
        val biometricManager = BiometricManager.from(requireContext())
        when(biometricManager.canAuthenticate()){
            BiometricManager.BIOMETRIC_SUCCESS -> {
                prompt = createBiometricPromptInstance()
                canAuthenticate = true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Timber.d("No biometric features avil")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Timber.d("currently unavalible")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Timber.d("No biometrics set up")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // quick and dirty button setup
        biometricButton = view.findViewById(R.id.biometric_button)
        biometricButton.setOnClickListener {
            if(canAuthenticate){
                prompt.authenticate(createPrompt())
            } else {
                Toast.makeText(context,"Can't do anything", Toast.LENGTH_LONG).show()
            }
        }
    }

    // callback from biometric results come in here.
    private fun createBiometricPromptInstance(): BiometricPrompt {

        val executer =  ContextCompat.getMainExecutor(requireContext())
        val callback = object: BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                view?.findViewById<TextView>(R.id.results_field)?.text = ""
                view?.findViewById<TextView>(R.id.results_field)?.text = "Success!"
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(requireContext(),"Failed", Toast.LENGTH_LONG).show();
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(requireContext(),"Error $errString", Toast.LENGTH_LONG).show();
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}