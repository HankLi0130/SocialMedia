package dev.hankli.iamstar.data.models

import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

class Profile(
    objectId: String = EMPTY,
    var displayName: String? = null,
    var description: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var birthday: Date? = null,
    var loginMethod: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var photoURL: String? = null,
    var sex: String? = null,
    var fcmToken: String? = null,
    var voipToken: String? = null,
    var createdAt: Date = Date(),
    var updatedAt: Date? = null
    // TODO AppIdentifiers
) : FirestoreModel(objectId)