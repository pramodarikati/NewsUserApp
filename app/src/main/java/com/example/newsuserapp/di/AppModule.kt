package com.example.newsuserapp.di


import android.content.Context
import com.example.newsuserapp.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(application: MyApplication): Context {
        return application.applicationContext
    }
}
