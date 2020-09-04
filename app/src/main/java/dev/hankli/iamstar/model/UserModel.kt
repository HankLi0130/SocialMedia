package dev.hankli.iamstar.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var userID: String = "",
    var profilePictureURL: String = "",
    var phoneNumber: String = "",
    var active: Boolean = false,
    var firstName: String = "Deleted User",
    var lastName: String? = "",
    var email: String? = "",
    var fcmToken: String = "",
    var userName: String = "",
    var isSelected: Boolean = false,
    @set:PropertyName("isAdmin")
    @get:PropertyName("isAdmin")
    var isAdmin: Boolean = false,
    var lastOnlineTimestamp: Timestamp? = null,
    var allowPushNotifications: Boolean = true
) : Parcelable {
    fun fullName(): String = "$firstName $lastName"
}