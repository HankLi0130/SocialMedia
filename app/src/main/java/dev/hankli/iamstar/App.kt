package dev.hankli.iamstar

import android.app.Application
import dev.hankli.iamstar.model.UserModel

class App : Application() {

    companion object {
        var currentUser: UserModel? = null
    }
}