package dev.hankli.iamstar.data.models

import tw.hankli.brookray.constant.EMPTY

class Profile(
    objectId: String = EMPTY,
    var uid: String = EMPTY,
) : FirestoreModel(objectId)