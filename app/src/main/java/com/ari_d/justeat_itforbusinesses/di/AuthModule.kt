package com.ari_d.justeat_itforbusinesses.di

import com.ari_d.justeat_itforbusinesses.ui.Auth.Repositories.AuthReposirory
import com.ari_d.justeat_itforbusinesses.ui.Auth.Repositories.AuthRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthRepository() = AuthRepositoryImplementation() as AuthReposirory
}