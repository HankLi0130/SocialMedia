package app.hankdev.utils.ext

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

fun Context.isInternetConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnected == true
}