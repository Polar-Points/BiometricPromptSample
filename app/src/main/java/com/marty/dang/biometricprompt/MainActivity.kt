package com.marty.dang.biometricprompt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// The new Biometrics API replaces the outdated fingerprintCompat API. This new library is very easy to implement but
// at the moment, it lacks the control you would have if you just used fingerprintCompat.
// 1. Not able to customize the biometric prompt interface
// 2. If a device has both fingerprint scanner and face scanner, which one is used? Can we control that?
// before P, it would use fingerprints though after android chooses the best one. So we don't have fine grain control like fingerprintCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.frag_container, MainFrag())
            .commit()
    }
}