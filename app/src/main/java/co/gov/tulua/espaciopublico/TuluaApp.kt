package co.gov.tulua.espaciopublico

import android.app.Application
import co.gov.tulua.espaciopublico.core.map.OsmConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TuluaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        OsmConfig.init(this)
    }
}
