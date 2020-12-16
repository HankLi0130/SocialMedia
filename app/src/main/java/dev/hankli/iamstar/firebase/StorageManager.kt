package dev.hankli.iamstar.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.InputStream

/**
 *  https://firebase.google.com/docs/storage/android/upload-files
 */
object StorageManager {

    private val storage by lazy { FirebaseStorage.getInstance() }

    suspend fun uploadFile(path: String, uri: Uri): String {
        val ref = storage.reference.child(path)
        return ref.putFile(uri)
            .continueWithTask { ref.downloadUrl }
            .await()
            .toString()
    }

    suspend fun uploadFile(path: String, bytes: ByteArray): String {
        val ref = storage.reference.child(path)
        return ref.putBytes(bytes)
            .continueWithTask { ref.downloadUrl }
            .await()
            .toString()
    }

    suspend fun uploadFile(path: String, stream: InputStream): String {
        val ref = storage.reference.child(path)
        return ref.putStream(stream)
            .continueWithTask { ref.downloadUrl }
            .await()
            .toString()
    }

    suspend fun deleteFile(path: String) = storage.reference.child(path).delete().await()
}