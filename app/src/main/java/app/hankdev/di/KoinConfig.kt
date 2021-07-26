package app.hankdev.di

import app.hankdev.BuildConfig
import app.hankdev.firestore.*
import app.hankdev.network.FcmApi
import app.hankdev.network.getRetrofit
import app.hankdev.repo.AuthRepo
import app.hankdev.repo.FeedRepo
import app.hankdev.repo.ProfileRepo
import app.hankdev.repo.ScheduleRepo
import app.hankdev.ui.auth.AuthViewModel
import app.hankdev.ui.comment.CommentViewModel
import app.hankdev.ui.feed.EditFeedViewModel
import app.hankdev.ui.feed.FeedDetailViewModel
import app.hankdev.ui.feed.FeedViewModel
import app.hankdev.ui.profile.EditProfileViewModel
import app.hankdev.ui.profile.ProfileViewModel
import app.hankdev.ui.schedule.EditScheduleViewModel
import app.hankdev.ui.schedule.ScheduleViewModel
import app.hankdev.utils.SharedPreferencesManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

val firebaseModule = module {
    single { FirebaseMessaging.getInstance() }
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
    single { ApplicationManager(db.collection(COLLECTION_APPLICATION)) }
    single {
        InstallationManager(
            get<ApplicationManager>().getSubcollection(
                BuildConfig.FS_APPLICATION_ID,
                COLLECTION_INSTALLATION
            )
        )
    }
    single { FeedManager(db.collection(COLLECTION_FEED)) }
    single { InfluencerManager(db.collection(COLLECTION_INFLUENCER)) }
    single { ProfileManager(db.collection(COLLECTION_PROFILE)) }
    single { ScheduleManager(db.collection(COLLECTION_SCHEDULE)) }
}

val repoModule = module {
    single { AuthRepo(get(), get(), get(), get()) }
    single { FeedRepo(get(), get(), get(), get(), get()) }
    single { ProfileRepo(get()) }
    single { ScheduleRepo(get(), get(), get()) }
}

val viewModelModule = module {
    // auth
    viewModel { AuthViewModel(get(), get()) }
    // feed
    viewModel { FeedViewModel(get(), get()) }
    viewModel { FeedDetailViewModel(get(), get()) }
    viewModel { EditFeedViewModel(get(), get()) }
    // comment
    viewModel { CommentViewModel(get()) }
    // profile
    viewModel { EditProfileViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    // schedule
    viewModel { ScheduleViewModel(get(), get()) }
    viewModel { EditScheduleViewModel(get(), get()) }
}

val koinModules =
    listOf(firebaseModule, appModule, networkModule, managerModule, repoModule, viewModelModule)