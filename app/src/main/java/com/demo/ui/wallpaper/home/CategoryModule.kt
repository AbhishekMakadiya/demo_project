package com.demo.ui.wallpaper.home

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object CategoryModule {
    @Provides
    fun provideCallback(activity: Activity) = activity as HomeCategoryAdapter.Callback
}