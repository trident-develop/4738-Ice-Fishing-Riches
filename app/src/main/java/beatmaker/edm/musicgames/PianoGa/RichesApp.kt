package beatmaker.edm.musicgames.PianoGa

import android.app.Application
import beatmaker.edm.musicgames.PianoGa.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RichesApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@RichesApp)
            modules(appModule)
        }
    }
}