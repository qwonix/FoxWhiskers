package ru.qwonix.android.foxwhiskers

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class FoxWhiskersApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPKIT_KEY)
        MapKitFactory.initialize(this)
    }
}