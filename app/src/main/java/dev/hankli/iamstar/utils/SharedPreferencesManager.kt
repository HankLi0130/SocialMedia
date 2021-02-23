package dev.hankli.iamstar.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dev.hankli.iamstar.data.models.Profile

class SharedPreferencesManager(private val context: Context) {

    companion object {
        private const val NORMAL = "normal"
    }

    fun getSp(name: String = NORMAL, mode: Int = MODE_PRIVATE) =
        context.getSharedPreferences(name, mode)

    fun getFcmToken(): String? = getSp().getString(Profile.FCM_TOKEN, null)

    fun saveFcmToken(token: String) = getSp().edit().apply {
        putString(Profile.FCM_TOKEN, token)
        apply()
    }

    fun remoeFcmToken() = getSp().edit().remove(Profile.FCM_TOKEN).apply()
}