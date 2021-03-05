package tw.iamstar.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import tw.iamstar.data.models.Profile

class SharedPreferencesManager(private val context: Context) {

    companion object {
        private const val NORMAL = "normal"
    }

    private fun getSp() = context.getSharedPreferences(NORMAL, MODE_PRIVATE)

    private fun removeKey(key: String) = getSp().edit().remove(key).apply()

    private fun isKeyExists(key: String) = getSp().contains(key)

    fun getFcmToken(): String? = getSp().getString(Profile.FCM_TOKEN, null)

    fun saveFcmToken(token: String) = getSp().edit().apply {
        putString(Profile.FCM_TOKEN, token)
        apply()
    }

    fun remoeFcmToken() = removeKey(Profile.FCM_TOKEN)

    fun fcmTokenExists() = isKeyExists(Profile.FCM_TOKEN)
}