package dev.hankli.iamstar.utils

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import dev.hankli.iamstar.model.UserModel
import dev.hankli.iamstar.utils.Consts.USERS

object FirestoreUtil {
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage by lazy { FirebaseStorage.getInstance().reference }
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val currentUserDocRef: DocumentReference
        get() = db.collection(USERS).document(
            auth.currentUser?.uid
                ?: throw NullPointerException("UID is null.")
        )
//    private val channelParticipationCollectionRef = db.collection("channel_participation")
//    private val channelsCollectionRef = db.collection(CHANNELS)

    fun getUserByID(
        id: String,
        completeListener: OnCompleteListener<DocumentSnapshot>
    ) {
        db.collection(USERS).document(id).get().addOnCompleteListener(completeListener)
    }

    fun getCurrentUser(
        onSuccessListener: OnSuccessListener<DocumentSnapshot>,
        onFailureListener: OnFailureListener
    ) {
        currentUserDocRef.get()
            .addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener)
    }

    fun updateUser(userModel: UserModel, onComplete: (String) -> Unit) {

        val task = currentUserDocRef.set(userModel)

        task.continueWith {
            if (it.isSuccessful) {
                onComplete("success")
            }
        }.addOnFailureListener {
            onComplete("failure")
        }
    }

//    fun addMessageListener(
//        channelId: String,
//        onComplete: (MutableList<MessageData>) -> Unit
//    ): ListenerRegistration {
//        return channelsCollectionRef.document(channelId).collection("thread").orderBy("created")
//            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                if (firebaseFirestoreException != null) {
//                    Log.e("FIRESTORE", "Pending listener error.", firebaseFirestoreException)
//                    return@addSnapshotListener
//                }
//
//                val items = mutableListOf<MessageData>()
//                querySnapshot!!.documents.forEach {
//                    val message = it.toObject(MessageData::class.java)!!
//                    message.messageID = it.id
//                    items.add(message)
//                }
//                onComplete(items)
//            }
//    }

//    fun getChannelByIdOrNull(channelId: String, onComplete: (ConversationModel?) -> Unit) {
//        db.collection(CHANNELS).document(channelId).get().addOnSuccessListener {
//            if (it.exists()) {
//                onComplete(it.toObject(ConversationModel::class.java))
//            } else {
//                onComplete(null)
//            }
//        }.addOnFailureListener {
//            onComplete(null)
//        }
//    }
//
//    fun createChannelParticipation(
//        participation: ChannelParticipation,
//        onComplete: (Boolean) -> Unit
//    ) {
//
//        val task = channelParticipationCollectionRef.add(participation)
//
//        task.continueWith {
//            if (it.isSuccessful) {
//                onComplete(true)
//            }
//        }.addOnFailureListener {
//            onComplete(false)
//        }
//
//    }
//
//    fun createChannel(
//        channelId: String,
//        channel: ConversationModel,
//        onComplete: (ConversationModel?) -> Unit
//    ) {
//        val task = channelsCollectionRef.document(channelId).set(channel)
//
//        task.continueWith {
//            if (it.isSuccessful) {
//                onComplete(channel)
//            }
//        }.addOnFailureListener {
//            onComplete(null)
//        }
//
//    }

    fun getUserToken(onComplete: (String?) -> Unit) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("test", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                onComplete(task.result?.token)
            })
    }

//    fun getUserListener(uid: String, onComplete: (UserModel?) -> Unit): ListenerRegistration {
//        return db.collection(USERS).document(uid)
//            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                if (firebaseFirestoreException != null) {
//                    Log.e("FIRESTORE", "Pending listener error.", firebaseFirestoreException)
//                    return@addSnapshotListener
//                }
//                val userModel = querySnapshot!!.toObject(UserModel::class.java)
//                onComplete(userModel)
//            }
//    }


//    fun sendMessage(message: MessageData, channelId: String, onComplete: (String) -> Unit) {
//        val threadRef = channelsCollectionRef.document(channelId).collection("thread")
//        message.messageID = threadRef.document().id
//        val task = threadRef.document(message.messageID).set(message)
//
//        task.continueWith {
//            if (it.isSuccessful) {
//                onComplete("success")
//            }
//        }.addOnFailureListener {
//            onComplete("failure")
//        }
//    }

//    fun updateMessage(conversationModel: ConversationModel, message: MessageData) {
//        db.collection(CHANNELS).document(conversationModel.id).collection("thread")
//            .document(message.messageID).update("seen", message.seen)
//    }
//    fun updateMessageSeener(conversationModel: ConversationModel, message: MessageData) {
//        db.collection(CHANNELS).document(conversationModel.id).collection("thread")
//            .document(message.messageID).update("lastMessageSeeners", message.lastMessageSeeners)
//    }

//    fun addChannelListener(
//        channelID: String,
//        onComplete: (ConversationModel) -> Unit
//    ): ListenerRegistration {
//        return db.collection(CHANNELS).document(channelID)
//            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
//                if (firebaseFirestoreException != null) {
//                    Log.e("FIRESTORE", "Pending listener error.", firebaseFirestoreException)
//                    return@addSnapshotListener
//                }
//
//                onComplete(documentSnapshot!!.toObject(ConversationModel::class.java)!!)
//            }
//    }


//    fun getGroupMembers(
//        channelId: String,
//        onComplete: (MutableList<String>) -> Unit
//    ): ListenerRegistration {
//
//
//        return channelParticipationCollectionRef.whereEqualTo("channel", channelId)
//            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//
//                if (firebaseFirestoreException != null) {
//                    Log.e("FIRESTORE", "Pending listener error.", firebaseFirestoreException)
//                    return@addSnapshotListener
//                }
//
//                val uids = mutableListOf<String>()
//                querySnapshot!!.documents.forEach {
//
//                    val model = it.toObject(ChannelParticipation::class.java)
//                    uids.add(model!!.user)
//                }
//                onComplete(uids)
//            }
//
//    }

//    fun updateChannel(channel: ConversationModel, onComplete: (String) -> Unit) {
//
//        val task = channelsCollectionRef.document(channel.id).set(channel)
//
//        task.continueWith {
//            if (it.isSuccessful) {
//                onComplete("success")
//            }
//        }.addOnFailureListener {
//            onComplete("failure")
//        }
//
//    }

//    fun leaveGroup(channelId: String, onComplete: (Boolean) -> Unit) {
//
//        channelParticipationCollectionRef.whereEqualTo("channel", channelId)
//            .whereEqualTo("user", MApplication.currentUser!!.userID).get().addOnSuccessListener {
//
//                channelParticipationCollectionRef.document(it.documents.first().id).delete()
//                    .addOnSuccessListener {
//                        onComplete(true)
//                    }.addOnFailureListener {
//                        onComplete(false)
//                    }
//            }.addOnFailureListener {
//                onComplete(false)
//            }
//    }

//    fun sendChatNotification(
//        token: String,
//        messageData: MessageData,
//        conversationModel: ConversationModel
//    ) {
//
//        val root = JSONObject()
//        val notification = JSONObject()
//        try {
//            val body = when {
//                messageData.url.isNotEmpty() -> {
//                    "${MApplication.currentUser!!.firstName} sent a new Image."
//                }
//                messageData.videoURL.isNotEmpty() -> {
//                    "${MApplication.currentUser!!.firstName} sent a new Video."
//                }
//                messageData.audioURL.isNotEmpty() -> {
//                    "${MApplication.currentUser!!.firstName} sent a new record."
//                }
//                else -> {
//                    "${MApplication.currentUser!!.firstName}: ${messageData.content}"
//                }
//            }
//            notification.put(
//                "body", body
//            )
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        try {
//            notification.put("title", MApplication.currentUser!!.firstName)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        try {
//            notification.put(
//                "tag",
//                conversationModel.id + ":" + MApplication.currentUser!!.firstName
//            )
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        val data = JSONObject()
//        try {
//            data.put("senderToken", MApplication.currentUser!!.fcmToken)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        try {
//            data.put("messageId", messageData.messageID)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        try {
//            root.put("notification", notification)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        try {
//            root.put("data", data)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        try {
//            root.put("to", token)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        val postData = root.toString()
//        Utils.openServerConnection(postData)
//    }

//    fun sendNotification(token: String, message: String) {
//
//        val root = JSONObject()
//        val notification = JSONObject()
//        try {
//            notification.put("body", MApplication.currentUser!!.firstName + message)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        try {
//            notification.put("title", "Friend request")
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//
//        val data = JSONObject()
//        try {
//            data.put("senderToken", MApplication.currentUser!!.fcmToken)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        try {
//            data.put("messageId", "0")
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        try {
//            root.put("notification", notification)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        try {
//            root.put("data", data)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        try {
//            root.put("to", token)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        val postData = root.toString()
//        Utils.openServerConnection(postData)
//    }

}