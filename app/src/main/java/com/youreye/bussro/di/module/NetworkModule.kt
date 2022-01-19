package com.youreye.bussro.di.module

import android.content.Context
import com.youreye.bussro.util.NetworkConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class NetworkModule {

    @Provides
    fun getNetworkClient(@ApplicationContext context: Context) =
        NetworkConnection(context)
}