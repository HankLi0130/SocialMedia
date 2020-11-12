package dev.hankli.iamstar.data.models

import tw.hankli.brookray.constant.EMPTY

data class Reaction(
    var objectId: String = EMPTY,
    var reaction: String? = null,
    var postId: String = EMPTY,
    var authorId: String = EMPTY
)