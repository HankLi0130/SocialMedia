package dev.hankli.iamstar.data.enums

import androidx.annotation.StringRes
import dev.hankli.iamstar.R

enum class Gender(@StringRes val stringRes: Int) {
    MALE(R.string.gender_male),
    FEMALE(R.string.gender_female)
}