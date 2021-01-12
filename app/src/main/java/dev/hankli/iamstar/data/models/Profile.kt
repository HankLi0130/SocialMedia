package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.PropertyName
import dev.hankli.iamstar.data.enums.Gender
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

class Profile(
    override var objectId: String = EMPTY,

    @get:PropertyName(DISPLAY_NAME)
    @set:PropertyName(DISPLAY_NAME)
    var displayName: String? = null,

    @get:PropertyName(DESCRIPTION)
    @set:PropertyName(DESCRIPTION)
    var description: String? = null,

    @get:PropertyName(FIRST_NAME)
    @set:PropertyName(FIRST_NAME)
    var firstName: String? = null,

    @get:PropertyName(LAST_NAME)
    @set:PropertyName(LAST_NAME)
    var lastName: String? = null,

    @get:PropertyName(BIRTHDAY)
    @set:PropertyName(BIRTHDAY)
    var birthday: Date? = null,

    @get:PropertyName(LOGIN_METHOD)
    @set:PropertyName(LOGIN_METHOD)
    var loginMethod: String? = null,

    @get:PropertyName(EMAIL)
    @set:PropertyName(EMAIL)
    var email: String? = null,

    @get:PropertyName(PHONE_NUMBER)
    @set:PropertyName(PHONE_NUMBER)
    var phoneNumber: String? = null,

    @get:PropertyName(PHOTO_URL)
    @set:PropertyName(PHOTO_URL)
    var photoURL: String? = null,

    @get:PropertyName(GENDER)
    @set:PropertyName(GENDER)
    var gender: Gender? = null,

    @get:PropertyName(FCM_TOKEN)
    @set:PropertyName(FCM_TOKEN)
    var fcmToken: String? = null,

    @get:PropertyName(VOIP_TOKEN)
    @set:PropertyName(VOIP_TOKEN)
    var voipToken: String? = null,

    @get:PropertyName(CREATED_AT)
    @set:PropertyName(CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(UPDATED_AT)
    @set:PropertyName(UPDATED_AT)
    var updatedAt: Date? = null
    // TODO AppIdentifiers
) : FirestoreModel {
    companion object {
        const val DISPLAY_NAME = "displayName"
        const val DESCRIPTION = "description"
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val BIRTHDAY = "birthday"
        const val LOGIN_METHOD = "loginMethod"
        const val EMAIL = "email"
        const val PHONE_NUMBER = "phoneNumber"
        const val PHOTO_URL = "photoURL"
        const val GENDER = "sex"
        const val FCM_TOKEN = "fcmToken"
        const val VOIP_TOKEN = "voipToken"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
}