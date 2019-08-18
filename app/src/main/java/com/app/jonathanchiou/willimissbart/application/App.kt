package com.app.jonathanchiou.willimissbart.application

import android.app.Application
import com.app.jonathanchiou.willimissbart.api.BartApiModule

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .bartApiModule(BartApiModule(this))
            .build()
    }

    override fun getSystemService(name: String): Any {
        if (name == AppComponent.serviceName) {
            return appComponent
        }

        return super.getSystemService(name)
    }
}