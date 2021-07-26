package app.hankdev.data.enums

import androidx.annotation.StringRes
import app.hankdev.R

enum class Gender(@StringRes val stringRes: Int) {
    MALE(R.string.gender_male),
    FEMALE(R.string.gender_female)
}