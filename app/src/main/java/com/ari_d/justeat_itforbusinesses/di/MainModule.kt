package com.ari_d.justeat_itforbusinesses.di

import com.ari_d.justeat_itforbusinesses.ui.Main.Repositories.MainRepositoryImplementation
import com.ari_d.justeat_itforbusinesses.ui.Main.Repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Singleton
    @Provides
    fun provideMainRepository() = MainRepositoryImplementation() as MainRepository
}