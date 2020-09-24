package dev.hankli.iamstar.data.models

import tw.hankli.brookray.constant.EMPTY
import tw.hankli.brookray.constant.ZERO

data class Media(
    var objectId: String = EMPTY,
    var url: String = EMPTY,
    var type: String = EMPTY,
    var height: Int = ZERO,
    var width: Int = ZERO
)