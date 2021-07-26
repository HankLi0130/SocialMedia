package app.hankdev.data.models.firestore

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

@IgnoreExtraProperties
class Profile(
    override var objectId: String = EMPTY,

    @get:PropertyName(Profile.Companion.DISPLAY_NAME)
    @set:PropertyName(Profile.Companion.DISPLAY_NAME)
    var displayName: String? = null,

    @get:PropertyName(Profile.Companion.DESCRIPTION)
    @set:PropertyName(Profile.Companion.DESCRIPTION)
    var description: String? = null,

    @get:PropertyName(Profile.Companion.FIRST_NAME)
    @set:PropertyName(Profile.Companion.FIRST_NAME)
    var firstName: String? = null,

    @get:PropertyName(Profile.Companion.LAST_NAME)
    @set:PropertyName(Profile.Companion.LAST_NAME)
    var lastName: String? = null,

    @get:PropertyName(Profile.Companion.BIRTHDAY)
    @set:PropertyName(Profile.Companion.BIRTHDAY)
    var birthday: Date? = null,

    @get:PropertyName(Profile.Companion.LOGIN_METHOD)
    @set:PropertyName(Profile.Companion.LOGIN_METHOD)
    var loginMethod: String? = null,

    @get:PropertyName(Profile.Companion.EMAIL)
    @set:PropertyName(Profile.Companion.EMAIL)
    var email: String? = null,

    @get:PropertyName(Profile.Companion.PHONE_NUMBER)
    @set:PropertyName(Profile.Companion.PHONE_NUMBER)
    var phoneNumber: String? = null,

    @get:PropertyName(Profile.Companion.PHOTO_URL)
    @set:PropertyName(Profile.Companion.PHOTO_URL)
    var photoURL: String? = null,

    @get:PropertyName(Profile.Companion.GENDER)
    @set:PropertyName(Profile.Companion.GENDER)
    var gender: app.hankdev.data.enums.Gender? = null,

    @get:PropertyName(Profile.Companion.CREATED_AT)
    @set:PropertyName(Profile.Companion.CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(Profile.Companion.UPDATED_AT)
    @set:PropertyName(Profile.Companion.UPDATED_AT)
    var updatedAt: Date? = null

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
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
}