package dev.hankli.iamstar.firebase

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Single
import java.io.InputStream

object StorageManager {

    val storage by lazy { FirebaseStorage.getInstance() }

    // https://firebase.google.com/docs/storage/android/upload-files
    fun uploadFile(path: String, uri: Uri): Single<String> {
        return Single.create { emitter ->
            val ref = storage.reference.child(path)
            ref.putFile(uri)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { emitter.onSuccess(it.toString()) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun uploadFile(path: String, bytes: ByteArray): Single<String> {
        return Single.create { emitter ->
            val ref = storage.reference.child(path)
            ref.putBytes(bytes)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { emitter.onSuccess(it.toString()) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun uploadFile(path: String, stream: InputStream): Single<String> {
        return Single.create { emitter ->
            val ref = storage.reference.child(path)
            ref.putStream(stream)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { emitter.onSuccess(it.toString()) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    // https://firebase.google.com/docs/storage/android/delete-files#kotlin+ktx
    fun deleteFile(path: String): Task<Void> {
        return storage.reference.child(path).delete()
    }
}