package tw.iamstar.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tw.iamstar.firebase.NotificationManager
import tw.iamstar.firestore.*
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
    single { NotificationManager(get(), get()) }
}

val managerModule = module {
    single<FeedManager> { FeedManager(db.collection(COLLECTION_FEED)) }
    single<InfluencerManager> { InfluencerManager(db.collection(COLLECTION_INFLUENCER)) }
    single<ProfileManager> { ProfileManager(db.collection(COLLECTION_PROFILE)) }
    single<ScheduleManager> { ScheduleManager(db.collection(COLLECTION_SCHEDULE)) }
}

val repoModule = module {
    single<FeedRepo> { FeedRepo(get(), get(), get()) }
    single<ProfileRepo> { ProfileRepo(get()) }
    single<ScheduleRepo> { ScheduleRepo(get(), get(), get()) }
}

val viewModelModule = module {
    // auth
    viewModel { AuthViewModel(get()) }
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