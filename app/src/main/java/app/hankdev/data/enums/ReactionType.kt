package app.hankdev.data.enums

import androidx.annotation.DrawableRes
import app.hankdev.R

enum class ReactionType(@DrawableRes val drawableRes: Int) {
    LIKE(R.drawable.ic_reaction_like),
    SURPRISED(R.drawable.ic_reaction_surprised),
    LAUGH(R.drawable.ic_reaction_laugh),
    SAD(R.drawable.ic_reaction_sad),
    ANGRY(R.drawable.ic_reaction_angry),
    LOVE(R.drawable.ic_reaction_love),
    NO_REACTION(R.drawable.ic_like)
}