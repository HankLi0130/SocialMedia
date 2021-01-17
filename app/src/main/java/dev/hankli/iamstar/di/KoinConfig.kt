package dev.hankli.iamstar.di

import com.google.firebase.firestore.FirebaseFirestore
import dev.hankli.iamstar.data.models.Influencer
import dev.hankli.iamstar.firestore.*
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.repo.ProfileRepo
import dev.hankli.iamstar.ui.auth.AuthViewModel
import dev.hankli.iamstar.ui.comment.CommentViewModel
import dev.hankli.iamstar.ui.feed.EditFeedViewModel
import dev.hankli.iamstar.ui.feed.FeedDetailViewModel
import dev.hankli.iamstar.ui.feed.FeedViewModel
import dev.hankli.iamstar.ui.profile.EditProfileViewModel
import dev.hankli.iamstar.ui.profile.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

val managerModule = module {
    single<FeedManager> { FeedManager(db.collection(COLLECTION_FEED)) }
    single<InfluencerManager> { InfluencerManager(db.collection(COLLECTION_INFLUENCER)) }
    single<ProfileManager> { ProfileManager(db.collection(COLLECTION_PROFILE)) }
}

val repoModule = module {
    single<FeedRepo> { FeedRepo(get(), get(), get()) }
    single<ProfileRepo> { ProfileRepo(get()) }
}

val viewModelModule = module {
    // auth
    viewModel { AuthViewModel(get()) }
    // feed
    viewModel { FeedViewModel(get(), get()) }
    viewModel { FeedDetailViewModel(get(), get()) }
    viewModel { EditFeedViewModel(get()) }
    // comment
    viewModel { CommentViewModel(get()) }
    // profile
    viewModel { EditProfileViewModel(get()) }
    viewModel { ProfileViewModel() }
}

val koinModules = listOf(managerModule, repoModule, viewModelModule)