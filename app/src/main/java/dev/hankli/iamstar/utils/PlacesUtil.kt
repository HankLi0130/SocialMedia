package dev.hankli.iamstar.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

val placesPermission = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION
)

private val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

fun getPlacesIntent(context: Context): Intent {
    return Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
        .build(context)
}