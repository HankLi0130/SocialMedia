package tw.iamstar.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import tw.iamstar.data.models.firestore.FirestoreModel

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

    fun getInstallationId(): String? = getSp(INSTALLATION).getString(FirestoreModel.OBJECT_ID, null)

    fun saveInstallationId(id: String) {
        getSpEdit(INSTALLATION) { putString(FirestoreModel.OBJECT_ID, id) }
    }

    fun remoeInstallationId() = removeKey(INSTALLATION, FirestoreModel.OBJECT_ID)

    fun installationIdExists() = isKeyExists(INSTALLATION, FirestoreModel.OBJECT_ID)

    fun installationIdNotExists() = !installationIdExists()
}