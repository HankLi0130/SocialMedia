package app.hankdev.di

import app.hankdev.firebase.AuthManager
import app.hankdev.firebase.MessagingManager
import app.hankdev.firebase.StorageManager
import app.hankdev.firestore.*
import app.hankdev.network.FcmApi
import app.hankdev.network.getRetrofit
import app.hankdev.repo.AuthRepo
import app.hankdev.repo.FeedRepo
import app.hankdev.repo.ProfileRepo
import app.hankdev.ui.SharedViewModel
import app.hankdev.ui.auth.AuthViewModel
import app.hankdev.ui.comment.CommentViewModel
import app.hankdev.ui.feed.EditFeedViewModel
import app.hankdev.ui.feed.FeedDetailViewModel
import app.hankdev.ui.feed.FeedViewModel
import app.hankdev.ui.profile.EditProfileViewModel
import app.hankdev.ui.profile.ProfileViewModel
import app.hankdev.ui.timeline.TimelineViewModel
import app.hankdev.utils.SharedPreferencesManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

val firebaseModule = module {
    single { FirebaseMessaging.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }
}

val appModule = module {
    single { SharedPreferencesManager(androidContext()) }
    single { Moshi.Builder().build() }
}

val networkModule = module {
    single { getRetrofit() }
    single { get<Retrofit>().create(FcmApi::class.java) }
}

val managerModule = module {
    single { InstallationManager(db.collection(COLLECTION_INSTALLATION)) }
    single { FeedManager(db.collection(COLLECTION_FEED)) }
    single { ProfileManager(db.collection(COLLECTION_PROFILE)) }
}

val repoModule = module {
    single { AuthRepo(get(), get(), get(), get(), get()) }
    single { FeedRepo(get(), get(), get(), get(), get()) }
    single { ProfileRepo(get(), get(), get()) }

    // Firebase
    single { AuthManager(get()) }
    single { MessagingManager(get()) }
    single { StorageManager(get()) }
}

val viewModelModule = module {
    // shared
    viewModel { SharedViewModel() }
    // timeline
    viewModel { TimelineViewModel(get()) }
    // auth
    viewModel { AuthViewModel(get()) }
    // feed
    viewModel { FeedViewModel(get(), get()) }
    viewModel { FeedDetailViewModel(get(), get()) }
    viewModel { EditFeedViewModel(get(), get(), get()) }
    // comment
    viewModel { CommentViewModel(get(), get()) }
    // profile
    viewModel { EditProfileViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
}

val koinModules =
    listOf(firebaseModule, appModule, networkModule, managerModule, repoModule, viewModelModule)