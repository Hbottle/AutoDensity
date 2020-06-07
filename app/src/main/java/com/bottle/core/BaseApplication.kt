package com.bottle.core

import android.app.Application
import com.bottle.core.arch.density.AutoDensity
import com.bottle.core.arch.density.DesignDraft

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AutoDensity.instance.init(this, DesignDraft(designSize = 360f))
    }
}