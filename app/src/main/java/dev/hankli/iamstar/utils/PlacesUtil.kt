package dev.hankli.iamstar.utils

import android.content.Context
import android.content.Intent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

private val fields = listOf(Place.Field.ID, Place.Field.NAME)

fun getPlacesIntent(context: Context): Intent {
    return Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
        .build(context)
}