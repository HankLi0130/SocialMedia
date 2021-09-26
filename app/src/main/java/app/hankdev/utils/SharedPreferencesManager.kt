package app.hankdev.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import app.hankdev.data.models.firestore.FirestoreModel

class SharedPreferencesManager(private val context: Context) {

    companion object {
        private const val INSTALLATION = "installation"
    }

    private fun getSp(spName: String) = context.getSharedPreferences(spName, MODE_PRIVATE)

    private fun getSpEdit(spName: String, block: SharedPreferences.Editor.() -> Unit) {
        getSp(spName).edit().apply { block() }.apply()
    }

    private fun removeKey(spName: String, key: String) {
        getSpEdit(spName) { remove(key) }
    }

    private fun isKeyExists(spName: String, key: String) = getSp(spName).contains(key)

    fun getInstallationId(): String? = getSp(INSTALLATION).getString(FirestoreModel.ID, null)

    fun saveInstallationId(id: String) {
        getSpEdit(INSTALLATION) { putString(FirestoreModel.ID, id) }
    }

    fun remoeInstallationId() = removeKey(INSTALLATION, FirestoreModel.ID)

    fun installationIdExists() = isKeyExists(INSTALLATION, FirestoreModel.ID)

    fun installationIdNotExists() = !installationIdExists()
}