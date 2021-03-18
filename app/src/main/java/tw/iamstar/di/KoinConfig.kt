package tw.iamstar.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tw.iamstar.BuildConfig
import tw.iamstar.firestore.*
import tw.iamstar.repo.FeedRepo
import tw.iamstar.repo.InstallationRepo
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
    single {
        FirebaseMessaging.getInstance()
    }
}

val appModule = module {
    single {
        SharedPreferencesManager(androidContext())
    }
}

val managerModule = module {
    single {
        InstallationManager(
            db.collection(COLLECTION_APPLICATION)
                .document(BuildConfig.FS_APPLICATION_ID)
                .collection(COLLECTION_INSTALLATION)
        )
    }
    single { FeedManager(db.collection(COLLECTION_FEED)) }
    single { InfluencerManager(db.collection(COLLECTION_INFLUENCER)) }
    single { ProfileManager(db.collection(COLLECTION_PROFILE)) }
    single { ScheduleManager(db.collection(COLLECTION_SCHEDULE)) }
}

val repoModule = module {
    single { InstallationRepo(get(), get()) }
    single { FeedRepo(get(), get(), get()) }
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
    viewModel { ProfileViewModel(get()) }
    // schedule
    viewModel { ScheduleViewModel(get(), get()) }
    viewModel { EditScheduleViewModel(get(), get()) }
}

val koinModules = listOf(firebaseModule, appModule, managerModule, repoModule, viewModelModule)