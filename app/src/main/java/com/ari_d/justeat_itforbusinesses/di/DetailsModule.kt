package com.ari_d.justeat_itforbusinesses.di

import com.ari_d.justeat_itforbusinesses.ui.Details.Repositories.DetailsRepository
import com.ari_d.justeat_itforbusinesses.ui.Details.Repositories.DetailsRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DetailsModule {

    @Singleton
    @Provides
    fun provideDetailsRepository() = DetailsRepositoryImplementation() as DetailsRepository
}