package com.app.jonathanchiou.willimissbart.application

import android.app.Application

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun getSystemService(name: String): Any? {
        if (name == AppComponent.serviceName) {
            return appComponent
        }

        return super.getSystemService(name)
    }
}
