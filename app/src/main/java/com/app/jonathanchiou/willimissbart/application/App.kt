package com.app.jonathanchiou.willimissbart.application

import android.app.Application
import android.content.Context
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
        if (name == serviceName) {
            return appComponent
        }

        return super.getSystemService(name)
    }

    companion object : ComponentDelegate<AppComponent>() {

        override val serviceName = "AppComponent"
    }
}