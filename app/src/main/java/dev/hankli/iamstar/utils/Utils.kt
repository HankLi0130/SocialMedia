package dev.hankli.iamstar.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import dev.hankli.iamstar.R

fun showDialog(context: Context, message: String) {
    val dialog = AlertDialog.Builder(context).apply {
        setMessage(message)
        setPositiveButton(context.getString(R.string.ok)) { _, _ -> }
        create()
    }
    dialog.show()
}