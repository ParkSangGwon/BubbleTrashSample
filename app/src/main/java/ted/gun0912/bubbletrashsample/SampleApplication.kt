package ted.gun0912.bubbletrashsample

import android.app.Application

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: SampleApplication
            private set
    }
}