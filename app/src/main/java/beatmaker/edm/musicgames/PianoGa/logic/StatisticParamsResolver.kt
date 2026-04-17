package beatmaker.edm.musicgames.PianoGa.logic

import android.content.Context
import beatmaker.edm.musicgames.PianoGa.audio.getDeviceString
import beatmaker.edm.musicgames.PianoGa.audio.getGadid
import beatmaker.edm.musicgames.PianoGa.audio.getRef
import beatmaker.edm.musicgames.PianoGa.audio.log
import beatmaker.edm.musicgames.PianoGa.audio.runProbe
import beatmaker.edm.musicgames.PianoGa.model.StatisticParameter
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class StatisticParamsResolver(
    private val context: Context
) {

    suspend fun resolveAll(): List<StatisticParameter> = coroutineScope {

        val result = listOf(

            async {
                val value = getRef(context)
//                log("Param: referrer = $value")
                StatisticParameter("4zg5fwj87h6", value)
            },

            async {
                val value = getGadid(context)
//                log("Param: gadid = $value")
                StatisticParameter("aqk9gw1949m", value)
            },

            async {
                val value = runProbe(context).toString()
//                val value = "0"
//                log("Param: probe = $value")
                StatisticParameter("avlky1pd0qvhnj6n0", value)
            },

            async {
                val value = getDeviceString()
//                log("Param: device = $value")
                StatisticParameter("jgccgk3qbi", value)
            },

            async {
                val value = runCatching {
                    Firebase.analytics.appInstanceId.await()
                }.getOrNull()

//                log("Param: externalId = $value")
                StatisticParameter("fxn8vjo3jx", value)
            },

            async {
                val value = runCatching {
                    val pi = context.packageManager
                        .getPackageInfo(context.packageName, 0)
                    pi.firstInstallTime.toString()
                }.getOrNull()

//                log("Param: install_time = $value")
                StatisticParameter("wpauwxub495", value)
            }

        ).awaitAll()

        result
    }
}