package dev.hankli.iamstar.utils

import android.content.Context
import androidx.annotation.DrawableRes
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.ReactionSelectedListener
import com.github.pgreze.reactions.ReactionsConfig
import com.github.pgreze.reactions.dsl.reactionConfig
import dev.hankli.iamstar.R

/**
 * https://github.com/pgreze/android-reactions
 */

data class Response(
    val name: String,
    @DrawableRes val icon: Int
)

val responses = arrayOf(
    Response("like", R.drawable.ic_reaction_like),
    Response("love", R.drawable.ic_reaction_love),
    Response("laugh", R.drawable.ic_reaction_laugh),
    Response("surprised", R.drawable.ic_reaction_surprised),
    Response("sad", R.drawable.ic_reaction_sad),
    Response("angry", R.drawable.ic_reaction_angry),
)

private fun getReactionConfig(context: Context): ReactionsConfig {
    return reactionConfig(context) {
        reactionsIds = responses.map { it.icon }.toIntArray()
    }
}

fun getReactionPopup(
    context: Context,
    config: ReactionsConfig = getReactionConfig(context),
    reactionSelectedListener: ReactionSelectedListener? = null
): ReactionPopup {
    return ReactionPopup(context, config, reactionSelectedListener)
}