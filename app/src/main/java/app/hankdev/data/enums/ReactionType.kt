package app.hankdev.data.enums

import androidx.annotation.DrawableRes
import app.hankdev.R

enum class ReactionType(@DrawableRes val drawableRes: Int) {
    LOVE(R.drawable.ic_baseline_favorite_24),
    NONE(R.drawable.ic_baseline_favorite_border_24)
}