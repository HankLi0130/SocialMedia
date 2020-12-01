package dev.hankli.iamstar.data.models

import tw.hankli.brookray.constant.EMPTY
import java.util.*

class Profile(
    objectId: String = EMPTY,
    var displayName: String = EMPTY,
    var description: String = EMPTY,
    var firstName: String = EMPTY,
    var lastName: String = EMPTY,
    var birthday: Date? = null,
    var loginMethod: String = EMPTY,
    var email: String = EMPTY,
    var phoneNumber: String = EMPTY,
    var photoURL: String = EMPTY,
    var sex: String = EMPTY,
    var fcmToken: String = EMPTY,
    var voipToken: String = EMPTY,
    var createdAt: Date = Date(),
    var updatedAt: Date? = null
    // TODO AppIdentifiers
) : FirestoreModel(objectId)