package beatmaker.edm.musicgames.PianoGa.audio

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

suspend fun getRef(context: Context): String = suspendCancellableCoroutine { continuation ->

    val client = InstallReferrerClient.newBuilder(context).build()
    val isResumed = AtomicBoolean(false)

    continuation.invokeOnCancellation {
        client.endConnection()
    }

    client.startConnection(object : InstallReferrerStateListener {
        override fun onInstallReferrerSetupFinished(responseCode: Int) {
            try {
                if (!isResumed.compareAndSet(false, true)) return

                val result = if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                    client.installReferrer.installReferrer
                } else {
                    "null"
                }

                continuation.resume(result)
//                continuation.resume("cmpgn=aaaa_TEST-Deeplink_bbbb_cccc_dddd")
                //                continuation.resume("cmpgn=test1_MA-TEST_22_33_sub5_sub6")
//                    continuation.resume("cmpgn=test1_CA-TEST_22_33_sub5_sub6")

            } catch (_: Exception) {
                if (isResumed.compareAndSet(false, true)) {
                    continuation.resume("null")
                }
            } finally {
                client.endConnection()
            }
        }

        override fun onInstallReferrerServiceDisconnected() {
            if (isResumed.compareAndSet(false, true)) {
                continuation.resume("null")
            }
        }
    })
}

private val EMPTY_GADID = "00000000-0000-0000-0000-000000000000"

private fun AdvertisingIdClient.Info?.safeId(): String {
    if (this == null) return EMPTY_GADID
    if (isLimitAdTrackingEnabled) return EMPTY_GADID
    return id?.takeIf { it.isNotBlank() } ?: EMPTY_GADID
}

suspend fun getGadid(context: Context): String =
    withContext(Dispatchers.IO) {
        runCatching {
            AdvertisingIdClient.getAdvertisingIdInfo(context)
        }.getOrNull().safeId()
    }

fun runProbe(context: Context): Int {
    fun res(): ContentResolver = context.contentResolver
    fun key(): String = Settings.Global.ADB_ENABLED
    fun defaultValue(): Int = 0
    fun read(resolver: ContentResolver, key: String, def: Int): Int {
        return Settings.Global.getInt(resolver, key, def)
    }
    return try {
        val raw = read(res(), key(), defaultValue())

        if (raw == 0) {
            0
        } else {
            throw IllegalStateException("Probe enabled")
        }

    } catch (e: Exception) {
        1
    }
}

fun getDeviceString(): String {
    return try {
        buildString {
            val brand = Build.BRAND.replaceFirstChar { it.titlecase(Locale.getDefault()) }
            append(brand).append(' ').append(Build.MODEL)
        }
    } catch (_: Throwable) {
        "unknown_device"
    }
}

private const val TAG = "MYTAG"

fun log(message: String) {
    Log.d(TAG, message)
}