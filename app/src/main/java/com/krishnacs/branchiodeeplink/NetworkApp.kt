package com.krishnacs.branchiodeeplink

import android.app.Application
import io.branch.referral.Branch
import io.branch.referral.BuildConfig

class NetworkApp: Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the Branch object
        //Branch.enableTestMode()
        Branch.enableLogging()
        Branch.getAutoInstance(this)
    }

}