package dev.hankli.iamstar.di

import com.google.firebase.firestore.FirebaseFirestore
import dev.hankli.iamstar.firestore.*
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.repo.ProfileRepo
import dev.hankli.iamstar.repo.ScheduleRepo
import dev.hankli.iamstar.ui.auth.AuthViewModel
import dev.hankli.iamstar.ui.comment.CommentViewModel
import dev.hankli.iamstar.ui.feed.EditFeedViewModel
import dev.hankli.iamstar.ui.feed.FeedDetailViewModel
import dev.hankli.iamstar.ui.feed.FeedViewModel
import dev.hankli.iamstar.ui.profile.EditProfileViewModel
import dev.hankli.iamstar.ui.profile.ProfileViewModel
import dev.hankli.iamstar.ui.schedule.EditScheduleViewModel
import dev.hankli.iamstar.ui.schedule.ScheduleViewModel
import dev.hankli.iamstar.utils.SharedPreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

val appModule = module {
    single { SharedPreferencesManager(androidContext()) }
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

val koinModules = listOf(appModule, managerModule, repoModule, viewModelModule)