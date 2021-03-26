package tw.iamstar.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import tw.iamstar.BuildConfig
import tw.iamstar.firestore.*
import tw.iamstar.network.FcmApi
import tw.iamstar.network.getRetrofit
import tw.iamstar.repo.AuthRepo
import tw.iamstar.repo.FeedRepo
import tw.iamstar.repo.ProfileRepo
import tw.iamstar.repo.ScheduleRepo
import tw.iamstar.ui.auth.AuthViewModel
import tw.iamstar.ui.comment.CommentViewModel
import tw.iamstar.ui.feed.EditFeedViewModel
import tw.iamstar.ui.feed.FeedDetailViewModel
import tw.iamstar.ui.feed.FeedViewModel
import tw.iamstar.ui.profile.EditProfileViewModel
import tw.iamstar.ui.profile.ProfileViewModel
import tw.iamstar.ui.schedule.EditScheduleViewModel
import tw.iamstar.ui.schedule.ScheduleViewModel
import tw.iamstar.utils.SharedPreferencesManager

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
    single { FeedRepo(get(), get(), get(), get()) }
    single { ProfileRepo(get()) }
    single { ScheduleRepo(get(), get(), get()) }
}

val viewModelModule = module {
    // auth
    viewModel { AuthViewModel(get(), get()) }
    // feed
    viewModel { FeedViewModel(get(), get(), get()) }
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