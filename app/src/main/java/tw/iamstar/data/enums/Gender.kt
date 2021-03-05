package tw.iamstar.data.enums

import androidx.annotation.StringRes
import tw.iamstar.R

enum class Gender(@StringRes val stringRes: Int) {
    MALE(R.string.gender_male),
    FEMALE(R.string.gender_female)
}